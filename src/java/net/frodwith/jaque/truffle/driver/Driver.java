package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.data.Cell;

public interface Driver {
  public Object apply(Cell core);
}
