package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

public class LiteralIntArrayNode extends SafeFormula {
  private int[] value;

  public LiteralIntArrayNode(int[] value) {
    this.value = value;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return value;
  }
  
  @Override
  public int[] executeIntArray(VirtualFrame frame, Object subject) {
    return value;
  }

  @Override
  public Object executeSubject(VirtualFrame frame, Object subject) {
    return value;
  }

}
