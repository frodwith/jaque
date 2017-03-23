package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.Bail;

public class IdentityNode extends FormulaNode {
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return getSubject(frame);
  }
}
