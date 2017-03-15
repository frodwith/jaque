package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ConditionProfile;

import net.frodwith.jaque.Bail;

public class IfNode extends FormulaNode {
  @Child private FormulaNode test;
  @Child private FormulaNode yes;
  @Child private FormulaNode no;
  private final ConditionProfile condition; 
  
  public IfNode(FormulaNode test, FormulaNode yes, FormulaNode no) {
    this.test = test;
    this.yes = yes;
    this.no = no;
    this.condition = ConditionProfile.createCountingProfile();
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    long answer;
    try {
      answer = test.executeLong(frame);
      if ( 0L == answer ) {
        condition.profile(true);
        return yes.executeGeneric(frame);
      }
      else if ( 1L == answer ) {
        condition.profile(false);
        return no.executeGeneric(frame);
      }
    }
    catch (UnexpectedResultException e) {
    }
    throw new Bail();
  }
}
