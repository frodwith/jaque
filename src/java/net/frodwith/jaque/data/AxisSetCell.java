package net.frodwith.jaque.data;

import java.util.Iterator;

public final class AxisSetCell extends AxisSet {
  private final AxisSet head;
  private final AxisSet tail;

  public AxisSetCell(AxisSet head, AxisSet tail) {
    this.head = head;
    this.tail = tail;
  }
  
  public boolean contains(Iterator<Fragment> i, boolean typed, boolean isCell) {
    if ( i.hasNext() ) {
      switch ( i.next() ) {
      case HEAD:
        return head.contains(i, typed, isCell);
      default:
        return tail.contains(i, typed, isCell);
      }
    }
    else {
      return !typed || isCell;
    }
  }
  
}
