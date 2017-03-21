package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.BranchProfile;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.nodes.NockDispatchNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNodeGen;

public final class NockNode extends FormulaNode {
  @Child private FormulaNode subject;
  @Child private FormulaNode formula;
  @Child private NockDispatchNode dispatch;
  private final BranchProfile errorPath;
  
  public NockNode(FormulaNode subject, FormulaNode formula, Context context, boolean tail) {
    this.subject = subject;
    this.formula = formula;
    this.dispatch = NockDispatchNodeGen.create(context, tail);
    this.errorPath = BranchProfile.create();
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object bus = subject.executeGeneric(frame);
    Cell fol;
    try {
      fol = formula.executeCell(frame);
      return dispatch.executeNock(frame, bus, fol);
    }
    catch (UnexpectedResultException e) {
      errorPath.enter();
      throw new Bail();
    }
  }
  

}
