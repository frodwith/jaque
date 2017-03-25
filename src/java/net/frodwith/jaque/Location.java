package net.frodwith.jaque;

import java.util.HashMap;
import java.util.Map;

import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.driver.Arm;
import net.frodwith.jaque.truffle.driver.AxisArm;
import net.frodwith.jaque.truffle.driver.NamedArm;
import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public final class Location {
  public final boolean isStatic;
  public final String name;
  public final String label;
  public final Object noun;
  public final Location parent;
  public final Object axisToParent;
  public final Map<String, Object> nameToAxis;
  public final Map<Object, String> axisToName;
  public final Map<Object,Class<? extends ImplementationNode>> drivers;
  
  public Location(String name, 
      String label,
      Object axisToParent,
      Map<String, Object> hooks, 
      Object noun, 
      Location parent,
      Arm[] arms) {

    this.name = name;
    this.label = label;
    this.parent = parent;
    this.axisToParent = axisToParent;
    this.nameToAxis = hooks;
    this.axisToName = new HashMap<Object, String>();
    this.isStatic = (null == parent) || (Noun.equals(3L, axisToParent) && parent.isStatic);
    this.noun = isStatic ? noun : TypesGen.asCell(noun).head;
    

    for ( Map.Entry<String, Object> e : hooks.entrySet() ) {
      axisToName.put(e.getValue(), e.getKey());
    }

    this.drivers = new HashMap<Object, Class<? extends ImplementationNode>>();
    if ( null != arms ) {
      for ( Arm a : arms ) {
        Object axis;
        if ( a instanceof AxisArm ) {
          AxisArm aa = (AxisArm) a;
          axis = aa.axis;
        }
        else {
          NamedArm na = (NamedArm) a;
          axis = nameToAxis.get(na.name);
        }
        drivers.put(axis, a.driver);
      }
    }
  }
}
