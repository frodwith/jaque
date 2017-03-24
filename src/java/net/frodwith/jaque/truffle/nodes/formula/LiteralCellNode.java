package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;

import net.frodwith.jaque.data.Cell;

public class LiteralCellNode extends FormulaNode {
  private DynamicObject value;

  public LiteralCellNode(DynamicObject value) {
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
