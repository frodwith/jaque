package net.frodwith.jaque.data;

import java.util.Iterator;

public abstract class AxisSet {
  public abstract boolean contains(Iterator<Fragment> iterator, boolean typed, boolean isCell);
  
  public boolean contains(Axis a, boolean typed, boolean isCell) {
    return contains(a.iterator(), typed, isCell);
  }
  
  public boolean containsNoun(Axis a) {
    return contains(a, false, false);
  }
  
  public boolean containsAtom(Axis a) {
    return contains(a, true, false);
  }

  public boolean containsCell(Axis a) {
    return contains(a, true, true);
  }
}
