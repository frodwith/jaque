package net.frodwith.jaque;

import com.oracle.truffle.api.object.DynamicObject;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;

public class KickLabel {
  public final DynamicObject battery;
  public final Object axis;
  
  public KickLabel(DynamicObject battery, Object axis) {
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
      return (battery == k.battery) && Atom.equals(axis, k.axis);
    }
  }
}
