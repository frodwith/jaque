package net.frodwith.jaque.truffle;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.ExecutionContext;
import com.oracle.truffle.api.Truffle;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.driver.AxisArm;
import net.frodwith.jaque.truffle.driver.NamedArm;
import net.frodwith.jaque.truffle.driver.Driver;
import net.frodwith.jaque.truffle.driver.Specification;
import net.frodwith.jaque.truffle.nodes.JaqueRootNode;

public class Context extends ExecutionContext {
  
  private final Map<KickLabel, CallTarget> kicks;
  private final Map<Cell, CallTarget> nocks;
  private final Map<Cell, Location> locations;
  private final Map<KickLabel, Driver> drivers;
  private final Map<AxisKey, Driver> installedByAxis;
  private final Map<NameKey, Driver> installedByName;
  
  public Context(Specification[] drivers) {
    this.kicks = new HashMap<KickLabel, CallTarget>();
    this.nocks = new HashMap<Cell, CallTarget>();
    this.locations = new HashMap<Cell, Location>();
    this.drivers = new HashMap<KickLabel, Driver>();
    this.installedByAxis = new HashMap<AxisKey, Driver>();
    this.installedByName = new HashMap<NameKey, Driver>();
    
    for ( Specification s : drivers ) {
      if ( s instanceof AxisArm ) {
        AxisArm a = (AxisArm) s;
        AxisKey k = new AxisKey(a.label, a.axis);
        this.installedByAxis.put(k, a.driver);
      }
      else {
        NamedArm n = (NamedArm) s;
        NameKey k = new NameKey(n.label, n.name);
        this.installedByName.put(k, n.driver);
      }
    }
  }

  private static CallTarget makeTarget(Cell formula) {
    CompilerDirectives.transferToInterpreter();
    return Truffle.getRuntime().createCallTarget(new JaqueRootNode(NockLanguage.parseCell(formula)));
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
  
  public Driver find(Cell core, Object axis) {
    Cell battery = TypesGen.asCell(core.head);
    KickLabel label = new KickLabel(battery, axis);
    if ( drivers.containsKey(label) ) {
      return drivers.get(label);
    }
    else {
      Driver driver;
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
          NameKey nk = new NameKey(ll, name);
          driver = installedByName.get(nk);
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
