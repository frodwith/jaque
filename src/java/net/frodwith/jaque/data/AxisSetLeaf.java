package net.frodwith.jaque.data;

import java.util.Iterator;

public final class AxisSetLeaf extends AxisSet {
  public static AxisSetLeaf INSTANCE = new AxisSetLeaf();
  
  private AxisSetLeaf() {
  }

  @Override
  public boolean contains(Iterator<Fragment> iterator, boolean typed, boolean isCell) {
    return !iterator.hasNext() && (!typed || !isCell);
  }
}
