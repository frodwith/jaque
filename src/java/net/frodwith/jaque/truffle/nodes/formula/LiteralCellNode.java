package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public final class LiteralCellNode extends FormulaNode {
  private final Cell value;

  public LiteralCellNode(Cell value) {
    this.value = value;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return value;
  }

  @Override
  public Cell executeCell(VirtualFrame frame) {
    return value;
  }

}
