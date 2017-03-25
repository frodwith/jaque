package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class LiteralIntArrayNode extends FormulaNode {
  private final int[] value;

  public LiteralIntArrayNode(int[] value) {
    this.value = value;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return value;
  }
  
  @Override
  public int[] executeIntArray(VirtualFrame frame) {
    return value;
  }

}
