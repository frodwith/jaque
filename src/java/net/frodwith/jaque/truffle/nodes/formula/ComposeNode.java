package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ComposeNode extends FormulaNode {
  @Child private FormulaNode f;
  @Child private FormulaNode g;
  
  public ComposeNode(FormulaNode f, FormulaNode g) {
    this.f = f;
    this.g = g;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    setSubject(frame, f.executeGeneric(frame));
    return g.executeGeneric(frame);
  }
}
