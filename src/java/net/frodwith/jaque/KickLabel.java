package net.frodwith.jaque;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;

public class KickLabel {
  public final Cell battery;
  public final Object axis;
  
  public KickLabel(Cell battery, Object axis) {
    this.battery = battery;
    this.axis = axis;
  }
  
  public int hashCode() {
    return Cell.getMug(battery) ^ Atom.mug(axis);
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
