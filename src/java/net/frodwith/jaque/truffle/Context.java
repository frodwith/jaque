package net.frodwith.jaque.truffle;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.ExecutionContext;
import com.oracle.truffle.api.Truffle;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.driver.AxisArm;
import net.frodwith.jaque.truffle.driver.NamedArm;
import net.frodwith.jaque.truffle.driver.Specification;
import net.frodwith.jaque.truffle.nodes.JaqueRootNode;
import net.frodwith.jaque.truffle.nodes.formula.BumpNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.ComposeNode;
import net.frodwith.jaque.truffle.nodes.formula.ConsNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.DeepNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.EscapeNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.NockNode;
import net.frodwith.jaque.truffle.nodes.formula.PushNode;
import net.frodwith.jaque.truffle.nodes.formula.SameNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.hint.DiscardHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.FastHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.MemoHintNode;
import net.frodwith.jaque.truffle.nodes.formula.Formula;
import net.frodwith.jaque.truffle.nodes.formula.FragmentNode;
import net.frodwith.jaque.truffle.nodes.formula.IfNode;
import net.frodwith.jaque.truffle.nodes.formula.KickNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralCellNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralIntArrayNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralLongNode;
import net.frodwith.jaque.truffle.nodes.jet.JetNode;

public class Context {
  
  private final Map<KickLabel, CallTarget> kicks;
  private final Map<Cell, CallTarget> nocks;
  private final Map<Cell, Location> locations;
  private final Map<KickLabel,Class<? extends JetNode>> drivers;
  private final Map<AxisKey,Class<? extends JetNode>> installedByAxis;
  private final Map<NameKey,Class<? extends JetNode>> installedByName;
  
  public Context(Specification[] drivers) {
    this.kicks = new HashMap<KickLabel, CallTarget>();
    this.nocks = new HashMap<Cell, CallTarget>();
    this.locations = new HashMap<Cell, Location>();
    this.drivers = new HashMap<KickLabel, Class<? extends JetNode>>();
    this.installedByAxis = new HashMap<AxisKey, Class<? extends JetNode>>();
    this.installedByName = new HashMap<NameKey, Class<? extends JetNode>>();
    
    if ( null != drivers ) {
      for ( Specification s : drivers ) {
        if ( s instanceof AxisArm ) {
          AxisArm a = (AxisArm) s;
          AxisKey k = new AxisKey(a.label, a.axis);
          this.installedByAxis.put(k, a.jetClass);
        }
        else {
          NamedArm n = (NamedArm) s;
          NameKey k = new NameKey(n.label, n.name);
          this.installedByName.put(k, n.jetClass);
        }
      }
    }
  }
  
  public Formula parseCell(Cell src) {
    Object op  = src.head,
           arg = src.tail;

    if ( TypesGen.isCell(op) ) {
      return ConsNodeGen.create(
          parseCell(TypesGen.asCell(op)),
          parseCell(TypesGen.asCell(arg)));
    }
    else {
      switch ( (int) TypesGen.asLong(op) ) {
        case 0: {
          return new FragmentNode(arg);
        }
        case 1: {
          if ( TypesGen.isCell(arg) ) {
            return new LiteralCellNode(TypesGen.asCell(arg));
          }
          else if ( TypesGen.isLong(arg) ) {
            return new LiteralLongNode(TypesGen.asLong(arg));
          }
          else {
            return new LiteralIntArrayNode(TypesGen.asIntArray(arg));
          }
        }
        case 2: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);
          return new NockNode(this, parseCell(h), parseCell(t));
        }
        case 3:
          return DeepNodeGen.create(parseCell(TypesGen.asCell(arg)));
        case 4:
          return BumpNodeGen.create(parseCell(TypesGen.asCell(arg)));
        case 5: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);
          return SameNodeGen.create(parseCell(h), parseCell(t));
        }
        case 6: {
          Cell trel = TypesGen.asCell(arg),
               pair = TypesGen.asCell(trel.tail),
               one  = TypesGen.asCell(trel.head),
               two  = TypesGen.asCell(pair.head),
               tre  = TypesGen.asCell(pair.tail);

          return new IfNode(parseCell(one), parseCell(two), parseCell(tre));
        }
        case 7: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);

          return new ComposeNode(parseCell(h), parseCell(t));
        }
        case 8: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);
          return new PushNode(parseCell(h), parseCell(t));
        }
        case 9: {
          Cell c = TypesGen.asCell(arg),
               t = TypesGen.asCell(c.tail);
          return new KickNode(this, c.head, parseCell(t));
        }
        case 10: {
          Cell    cell = TypesGen.asCell(arg);
          Formula next = parseCell(TypesGen.asCell(cell.tail));
          Object  head = cell.head;
          if ( Noun.isAtom(head) ) {
            if ( Atom.MEMO.equals(head) ) {
              return new MemoHintNode(next);
            }
            else {
              // What do you do with static hints you don't recognize? Nothing...
              return next;
            }
          }
          else {
            Cell dyn     = TypesGen.asCell(head);
            Formula dynF = parseCell(TypesGen.asCell(dyn.tail));
            Object kind  = dyn.head;
            if ( Atom.FAST.equals(kind) ) {
              return new FastHintNode(this, dynF, next);
            }
            else {
              return new DiscardHintNode(dynF, next);
            }
          }
        }
        case 11: {
          Cell c = TypesGen.asCell(arg);
          return EscapeNodeGen.create(
              parseCell(TypesGen.asCell(c.head)),
              parseCell(TypesGen.asCell(c.tail)),
              this);
        }
        default: {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  public Object nock(Object subject, Cell formula) {
    return getNock(formula).call(subject);
  }

  private CallTarget makeTarget(Cell formula) {
    CompilerDirectives.transferToInterpreter();
    return Truffle.getRuntime().createCallTarget(new JaqueRootNode(parseCell(formula)));
  }
  
  public CallTarget getNock(Cell c) {
    CallTarget t = nocks.get(c);
    if ( null == t ) {
      t = makeTarget(c);
      nocks.put(c, t);
    }
    return t;
  }

  public CallTarget getKick(Cell core, Object axis) {
    Cell battery    = TypesGen.asCell(core.head);
    KickLabel label = new KickLabel(battery, axis);
    CallTarget t    = kicks.get(label);
    if ( null == t ) {
      Object obj = Noun.fragment(axis, core);
      if ( !TypesGen.isCell(obj) ) {
        throw new Bail();
      }
      else {
        t = makeTarget(TypesGen.asCell(obj));
        kicks.put(label, t);
      }
    }
    return t;
  }
  
  public boolean fine(Cell core) {
    Cell battery = TypesGen.asCell(core.head);
    Location loc = locations.get(battery);
    return ( null != loc ) && loc.matches(core);
  }
  
  public void register(Cell core, String name, Object parentAxis, Map<String, Object> hooks) {
    Cell battery = TypesGen.asCell(core.head);
    if ( Atom.isZero(parentAxis) ) {
      locations.put(battery, new StaticLocation(name, core, hooks));
    }
    else {
      Cell parentCore = TypesGen.asCell(Noun.fragment(parentAxis, core));
      Cell parentBattery = TypesGen.asCell(parentCore.head);
      Location parentLocation = locations.get(parentBattery);
      if ( null == parentLocation ) {
        System.err.println("register: invalid parent");
      }
      else {
        locations.put(battery, new DynamicLocation(
            battery, name, parentAxis, parentLocation, hooks));
      }
    }
  }
  
  public Class<? extends JetNode> find(Cell core, Object axis) {
    Cell battery = TypesGen.asCell(core.head);
    KickLabel label = new KickLabel(battery, axis);
    if ( drivers.containsKey(label) ) {
      return drivers.get(label);
    }
    else {
      Class<? extends JetNode> driver;
      Location loc = locations.get(battery);
      if ( null == loc ) {
        driver = null;
      }
      else {
        String ll = loc.getLabel();
        AxisKey ak = new AxisKey(ll, axis);
        driver = installedByAxis.get(ak);
        if ( null == driver ) {
          String name = loc.axisHook(axis);
          if ( null != name ) {
            NameKey nk = new NameKey(ll, name);
            driver = installedByName.get(nk);
          }
        }
      }
      drivers.put(label, driver);
      return driver;
    }
  }
  
  private abstract class Location {
    protected String name;
    private Map<String, Object> nameToAxis;
    private Map<Object, String> axisToName;

    protected Location(String name, Map<String, Object> hooks) {
      this.name = name;
      this.nameToAxis = hooks;
      this.axisToName = new HashMap<Object, String>();
      for ( Map.Entry<String, Object> e : hooks.entrySet() ) {
        axisToName.put(e.getValue(), e.getKey());
      }
    }

    public abstract boolean matches(Cell core);
    public abstract String getLabel();
    
    public Object hookAxis(String name) {
      return nameToAxis.get(name);
    }
    
    public String axisHook(Object axis) {
      return axisToName.get(axis);
    }
  }
  
  private class StaticLocation extends Location {
    private final Object noun;
    
    public StaticLocation(String name, Object noun, Map<String, Object> hooks) {
      super(name, hooks);
      this.noun = noun;
    }

    public boolean matches(Cell core) {
      return Noun.equals(noun, core);
    }
    
    public String getLabel() {
      return name;
    }
  }
  
  private class DynamicLocation extends Location {
    private final Cell battery;
    private final Object parentAxis;
    private final Location parent;
    private final String label;
    
    public DynamicLocation(Cell battery, String name,
        Object parentAxis, Location parent, Map<String, Object> hooks) {
      super(name, hooks);
      this.battery = battery;
      this.label = parent.getLabel() + "/" + name;
      this.parentAxis = parentAxis;
      this.parent = parent;
    }
    
    public boolean matches(Cell core) {
      return Noun.equals(core.head, battery)
          && parent.matches(TypesGen.asCell(Noun.fragment(parentAxis, core)));
    }
    
    public String getLabel() {
      return label;
    }
  }

  private class NameKey {
    public final String label;
    public final String name;
    
    public NameKey(String label, String name) {
      this.label = label;
      this.name = name;
    }
    
    public int hashCode() {
      return label.hashCode() ^ name.hashCode();
    }
    
    public boolean equals(Object o) {
      if ( o instanceof NameKey ) {
        NameKey d = (NameKey) o;
        return d.label == label && d.name == name;
      }
      else {
        return false;
      }
    }
  }
  
  private class AxisKey {
    public final String label;
    public final Object axis;
    
    public AxisKey(String label, Object axis) {
      this.label = label;
      this.axis = axis;
    }
    
    public int hashCode() {
      return label.hashCode() ^ Atom.mug(axis);
    }
    
    public boolean equals(Object o) {
      if ( o instanceof AxisKey ) {
        AxisKey d = (AxisKey) o;
        return d.label == label && Atom.equals(d.axis, axis);
      }
      else {
        return false;
      }
    }
  }
  
  private class KickLabel {
    public final Cell battery;
    public final Object axis;
    
    public KickLabel(Cell battery, Object axis) {
      this.battery = battery;
      this.axis = axis;
    }
    
    public int hashCode() {
      return Cell.mug(battery) ^ Atom.mug(axis);
    }
    
    public boolean equals(Object o) {
      if ( !(o instanceof KickLabel) ) {
        return false;
      }
      else {
        KickLabel k = (KickLabel) o;
        return Cell.equals(battery, k.battery) && Atom.equals(axis, k.axis);
      }
    }
  }
}
