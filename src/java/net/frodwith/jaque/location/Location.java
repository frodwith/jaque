package net.frodwith.jaque.location;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Fragmenter;
import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public abstract class Location {
  protected final String name;
  private final Map<String, Object> nameToAxis;
  private final Map<Object, String> axisToName;
  public final Map<Object, Class<? extends ImplementationNode>> drivers;
  public final Fragmenter[] fragments;

  protected Location(String name, Map<String, Object> hooks, Object[] axes) {
    this.name = name;
    this.nameToAxis = hooks;
    this.axisToName = new HashMap<Object, String>();
    this.drivers = new HashMap<Object, Class<? extends ImplementationNode>>();
    for ( Map.Entry<String, Object> e : hooks.entrySet() ) {
      axisToName.put(e.getValue(), e.getKey());
    }

    this.fragments = new Fragmenter[axes.length];
    int i = 0;
    for ( Object axis : axes ) {
      this.fragments[i++] = new Fragmenter(axis);
    }
  }

  public abstract boolean matches(Cell core);
  public abstract String getLabel();
  protected abstract Object reconstructInner(Object[] arguments, int index);
  
  public  Object reconstruct(Object[] arguments) {
    return reconstructInner(arguments, 0);
  }
  
  public Object hookAxis(String name) {
    return nameToAxis.get(name);
  }
  
  public String axisHook(Object axis) {
    return axisToName.get(axis);
  }
  
  public Class<? extends ImplementationNode> find(Fragmenter part) {
    return drivers.get(part.axis);
  }
  
  public void install(Fragmenter part, Class<? extends ImplementationNode> driver) {
    assert !drivers.containsKey(part.axis);
    drivers.put(part.axis, driver);
  }
}
