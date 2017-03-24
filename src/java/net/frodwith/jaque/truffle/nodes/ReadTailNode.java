package net.frodwith.jaque.truffle.nodes;

import net.frodwith.jaque.data.Cell;

public final class ReadTailNode extends ReadNode {
  @Override
  public Object executeRead(Cell c) {
    return c.tail;
  }
}
