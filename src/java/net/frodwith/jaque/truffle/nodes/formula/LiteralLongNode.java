package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class LiteralLongNode extends FormulaNode {
  private final long value;

  public LiteralLongNode(long value) {
    this.value = value;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return value;
  }

  @Override
  public long executeLong(VirtualFrame frame) {
    return value;
  }

}
