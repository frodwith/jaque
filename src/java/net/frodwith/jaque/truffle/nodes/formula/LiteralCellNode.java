package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public class LiteralCellNode extends FormulaNode {
  private Cell value;

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
