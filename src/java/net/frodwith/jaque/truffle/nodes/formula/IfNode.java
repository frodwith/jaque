package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ConditionProfile;

import net.frodwith.jaque.truffle.Bail;

public class IfNode extends JumpFormula {
  @Child private Formula test;
  @Child private Formula yes;
  @Child private Formula no;
  private final ConditionProfile condition; 
  
  public IfNode(Formula test, Formula yes, Formula no) {
    this.test = test;
    this.yes = yes;
    this.no = no;
    this.condition = ConditionProfile.createCountingProfile();
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object subject = getSubject(frame);
    long answer;
    try {
      answer = test.executeLong(frame);
      setSubject(frame, subject);
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
