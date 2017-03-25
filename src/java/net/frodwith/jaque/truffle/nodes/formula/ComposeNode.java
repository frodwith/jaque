package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class ComposeNode extends FormulaNode {
  @Child private FormulaNode f;
  @Child private FormulaNode g;
  
  public ComposeNode(FormulaNode f, FormulaNode g) {
    this.f = f;
    this.g = g;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object old = getSubject(frame);
    setSubject(frame, f.executeGeneric(frame));
    Object product = g.executeGeneric(frame);
    setSubject(frame, old);
    return product;
  }
}
