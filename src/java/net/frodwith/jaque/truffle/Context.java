package net.frodwith.jaque.truffle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Fragmenter;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.location.DynamicLocation;
import net.frodwith.jaque.location.Location;
import net.frodwith.jaque.location.StaticLocation;
import net.frodwith.jaque.truffle.driver.Arm;
import net.frodwith.jaque.truffle.driver.AxisArm;
import net.frodwith.jaque.truffle.driver.NamedArm;
import net.frodwith.jaque.truffle.driver.Specification;
import net.frodwith.jaque.truffle.nodes.DispatchNode;
import net.frodwith.jaque.truffle.nodes.DispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.FunctionNode;
import net.frodwith.jaque.truffle.nodes.JaqueRootNode;
import net.frodwith.jaque.truffle.nodes.TopRootNode;
import net.frodwith.jaque.truffle.nodes.formula.BumpNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.ComposeNode;
import net.frodwith.jaque.truffle.nodes.formula.ConsNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.DeepNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.EscapeNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.NockNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.PushNode;
import net.frodwith.jaque.truffle.nodes.formula.SameNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.hint.DiscardHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.FastHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.MemoHintNode;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;
import net.frodwith.jaque.truffle.nodes.formula.FragmentNode;
import net.frodwith.jaque.truffle.nodes.formula.IfNode;
import net.frodwith.jaque.truffle.nodes.formula.KickNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.LiteralCellNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralIntArrayNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralLongNode;
import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public class Context {
  
  private final Map<KickLabel, CallTarget> kicks;
  private final Map<Cell, CallTarget> nocks;
  private final Map<Cell, Location> locations;
  private final Map<String, Arm[]> drivers;
  
  public Context(Arm[] arms) {
    this.kicks = new HashMap<KickLabel, CallTarget>();
    this.nocks = new HashMap<Cell, CallTarget>();
    this.locations = new HashMap<Cell, Location>();
    this.drivers = new HashMap<String, Arm[]>();
    
    Map<String, List<Arm>> temp = new HashMap<String, List<Arm>>();
    if ( null != arms ) {
      for ( Arm a : arms ) {
        List<Arm> push = temp.get(a.label);
        if ( null == push ) {
          push = new LinkedList<Arm>();
          temp.put(a.label, push);
        }
        push.add(a);
      }
    }
    
    for ( Map.Entry<String, List<Arm>> e : temp.entrySet() ) {
      drivers.put(e.getKey(), e.getValue().toArray(new Arm[0]));
    }
  }
  
  public FormulaNode parseCell(Cell src, boolean tail) {
    Object op  = src.head,
           arg = src.tail;

    if ( TypesGen.isCell(op) ) {
      return ConsNodeGen.create(
          parseCell(TypesGen.asCell(op), false),
          parseCell(TypesGen.asCell(arg), false));
    }
    else {
      switch ( (int) TypesGen.asLong(op) ) {
        case 0: {
          Fragmenter fragmenter = new Fragmenter(arg);
          return new FragmentNode(fragmenter);
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
          return NockNodeGen.create(parseCell(h, false), parseCell(t, false), this, tail);
        }
        case 3:
          return DeepNodeGen.create(parseCell(TypesGen.asCell(arg), false));
        case 4:
          return BumpNodeGen.create(parseCell(TypesGen.asCell(arg), false));
        case 5: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);
          return SameNodeGen.create(
              parseCell(h, false),
              parseCell(t, false));
        }
        case 6: {
          Cell trel = TypesGen.asCell(arg),
               pair = TypesGen.asCell(trel.tail),
               one  = TypesGen.asCell(trel.head),
               two  = TypesGen.asCell(pair.head),
               tre  = TypesGen.asCell(pair.tail);

          return new IfNode(
              parseCell(one, false),
              parseCell(two, tail),
              parseCell(tre, tail));
        }
        case 7: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);

          return new ComposeNode(
              parseCell(h, false), 
              parseCell(t, tail));
        }
        case 8: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);
          return new PushNode(
              parseCell(h, false), 
              parseCell(t, tail));
        }
        case 9: {
          Cell c = TypesGen.asCell(arg),
               t = TypesGen.asCell(c.tail);
          Fragmenter fragmenter = new Fragmenter(c.head);
               
          return KickNodeGen.create(parseCell(t, false), this, tail, fragmenter.isLeft(), fragmenter);
        }
        case 10: {
          Cell    cell = TypesGen.asCell(arg);
          FormulaNode next = parseCell(TypesGen.asCell(cell.tail), tail);
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
            FormulaNode dynF = parseCell(TypesGen.asCell(dyn.tail), false);
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
              parseCell(TypesGen.asCell(c.head), false),
              parseCell(TypesGen.asCell(c.tail), false),
              this);
        }
        default: {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  public Object nock(Object subject, Cell formula) {
    TopRootNode top = new TopRootNode(getNock(formula));
    return Truffle.getRuntime().createCallTarget(top).call(subject);
  }

  private CallTarget makeTarget(Cell formula) {
    CompilerDirectives.transferToInterpreter();
    return Truffle.getRuntime().createCallTarget(new JaqueRootNode(parseCell(formula, true)));
  }
  
  public CallTarget getNock(Cell c) {
    CallTarget t = nocks.get(c);
    if ( null == t ) {
      t = makeTarget(c);
      nocks.put(c, t);
    }
    return t;
  }

  public CallTarget getKick(Cell core, Fragmenter fragmenter) {
    Cell battery    = TypesGen.asCell(core.head);
    KickLabel label = new KickLabel(battery, fragmenter.axis);
    CallTarget t    = kicks.get(label);
    if ( null == t ) {
      Object obj = fragmenter.fragment(core);
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
  
  public Location lookup(Cell core) {
    return locations.get(TypesGen.asCell(core.head));
  }
  
  public void register(Cell core, String name, Fragmenter toParent, Map<String, Object> hooks) {
    Cell battery = TypesGen.asCell(core.head);
    Location loc;
    if ( toParent.isZero() ) {
      loc = new StaticLocation(name, core, hooks);
    }
    else {
      Cell parentCore = TypesGen.asCell(toParent.fragment(core));
      Cell parentBattery = TypesGen.asCell(parentCore.head);
      Location parentLoc = locations.get(parentBattery);
      if ( null == parentLoc ) {
        System.err.println("register: invalid parent");
        return;
      }
      loc = new DynamicLocation(battery, name, toParent, parentLoc, hooks);
    }

    Arm[] arms = drivers.get(loc.getLabel());
    if ( arms != null ) {
      for ( Arm a : arms ) {
        Object axis;
        if ( a instanceof AxisArm ) {
          AxisArm aa = (AxisArm) a;
          axis = aa.axis;
        }
        else {
          NamedArm na = (NamedArm) a;
          axis = loc.hookAxis(na.name);
        }
        loc.install(new Fragmenter(axis), a.driver);
      }
    }

    locations.put(battery, loc);
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
