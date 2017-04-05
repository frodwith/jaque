package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public final class PushNode extends FormulaNode {
  @Child private FormulaNode f;
  @Child private FormulaNode g;
//  ValueProfile headProfile = ValueProfile.createClassProfile();
  
  public PushNode(FormulaNode f, FormulaNode g) {
    this.f = f;
    this.g = g;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object old = getSubject(frame);
//    Object head = headProfile.profile(f.executeGeneric(frame));
    Object head = f.executeGeneric(frame);
    setSubject(frame, new Cell(head, old));
    Object product = g.executeGeneric(frame);
    setSubject(frame, old);
    return product;
  }
}
