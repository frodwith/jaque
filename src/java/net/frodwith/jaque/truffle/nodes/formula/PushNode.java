package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public class PushNode extends JumpFormula {
  @Child private FormulaNode f;
  @Child private FormulaNode g;
  
  public PushNode(FormulaNode f, FormulaNode g) {
    this.f = f;
    this.g = g;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object tail = getSubject(frame);
    Object head = f.executeSafe(frame);
    setSubject(frame, new Cell(head, tail));
    return g.executeGeneric(frame);
  }
}
