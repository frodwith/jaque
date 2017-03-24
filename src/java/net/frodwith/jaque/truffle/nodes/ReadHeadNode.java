package net.frodwith.jaque.truffle.nodes;

import net.frodwith.jaque.data.Cell;

public final class ReadHeadNode extends ReadNode {
  @Override
  public Object executeRead(Cell c) {
    return c.head;
  }
}
