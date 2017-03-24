package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;

public class PushNode extends FormulaNode {
  @Child private FormulaNode f;
  @Child private FormulaNode g;
  
  public PushNode(FormulaNode f, FormulaNode g) {
    this.f = f;
    this.g = g;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object tail = getSubject(frame);
    Object head = f.executeGeneric(frame);
    setSubject(frame, Context.cons(head, tail));
    Object product = g.executeGeneric(frame);
    setSubject(frame, tail);
    return product;
  }
}
