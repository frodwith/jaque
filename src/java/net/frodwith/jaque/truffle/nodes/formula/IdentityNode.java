package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class IdentityNode extends FormulaNode {
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return getSubject(frame);
  }
}
