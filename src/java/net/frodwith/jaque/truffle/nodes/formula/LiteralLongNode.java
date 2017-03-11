package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

public class LiteralLongNode extends SafeFormula {
  private long value;

  public LiteralLongNode(long value) {
    this.value = value;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return value;
  }

  @Override
  public long executeLong(VirtualFrame frame, Object subject) {
    return value;
  }

  @Override
  public Object executeSubject(VirtualFrame frame, Object subject) {
    return value;
  }

}
