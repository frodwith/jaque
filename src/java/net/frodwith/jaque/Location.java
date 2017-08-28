package net.frodwith.jaque;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.TypesGen;

public final class Location implements Serializable {
  public final boolean isStatic;
  public final String name;
  public final String label;
  public final Object noun;
  public final Location parent;
  public final Object axisToParent;
  public final HashMap<String, Object> nameToAxis;
  public final HashMap<Object, String> axisToName;
  
  public Location(String name, 
      String label,
      Object axisToParent,
      HashMap<String, Object> hooks, 
      Object noun, 
      Location parent) {

    this.name = name;
    this.label = label;
    this.parent = parent;
    this.axisToParent = axisToParent;
    this.nameToAxis = hooks;
    this.axisToName = new HashMap<Object, String>();
    this.isStatic = (null == parent) || (Noun.equals(3L, axisToParent) && parent.isStatic);
    if ( isStatic ) {
      this.noun = noun;
    }
    else {
      try {
        Cell battery = TypesGen.expectCell(TypesGen.expectCell(noun).head);
        battery.calculateMug();
        this.noun = battery;
      }
      catch ( UnexpectedResultException e ) {
        throw new RuntimeException("Registering dynamic location of non-core");
      }
    }
    
    for ( Map.Entry<String, Object> e : hooks.entrySet() ) {
      axisToName.put(e.getValue(), e.getKey());
    }
    
  }
}
