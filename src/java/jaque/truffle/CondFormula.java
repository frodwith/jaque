package jaque.truffle;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;

import jaque.interpreter.Bail;
import jaque.noun.Atom;
import jaque.noun.Cell;

@NodeInfo(shortName = "cond")
public final class CondFormula extends UnsafeFormula {
  @Child private Formula test;
  @Child private Formula yes;
  @Child private Formula no;

  public CondFormula(Formula test, Formula yes, Formula no) {
    this.test = test;
    this.yes  = yes;
    this.no   = no;
  }

  private final ConditionProfile condition = ConditionProfile.createCountingProfile();

  @Override
  public Object execute(VirtualFrame frame) {
    boolean t;
    Object subject = getSubject(frame);
    try {
      t = condition.profile(test.executeBoolean(frame));
    }
    catch (UnexpectedResultException e) {
      throw new Bail();
    }
    setSubject(frame, subject);
    if ( t ) {
      return yes.execute(frame);
    }
    else {
      return no.execute(frame);
    }
  }

  public Cell toCell() {
    return new Cell(6L, new Cell(test.toCell(), new Cell(yes.toCell(), no.toCell())));
  }
}
