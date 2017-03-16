package net.frodwith.jaque.location;

import java.util.HashMap;
import java.util.Map;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Fragmenter;
import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public abstract class Location {
  protected final String name;
  private final Map<String, Object> nameToAxis;
  private final Map<Object, String> axisToName;
  private final Map<Object, Class<? extends ImplementationNode>> drivers;

  protected Location(String name, Map<String, Object> hooks) {
    this.name = name;
    this.nameToAxis = hooks;
    this.axisToName = new HashMap<Object, String>();
    this.drivers = new HashMap<Object, Class<? extends ImplementationNode>>();
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
  
  public Class<? extends ImplementationNode> find(Fragmenter part) {
    return drivers.get(part.axis);
  }
  
  public void install(Fragmenter part, Class<? extends ImplementationNode> driver) {
    assert !drivers.containsKey(part.axis);
    drivers.put(part.axis, driver);
  }
}
