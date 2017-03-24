package net.frodwith.jaque.truffle.nodes;

import net.frodwith.jaque.data.Cell;

public abstract class ReadNode extends JaqueNode {
  public abstract Object executeRead(Cell c);
}
