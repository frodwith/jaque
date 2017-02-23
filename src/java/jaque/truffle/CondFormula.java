package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;

@NodeInfo(shortName = "cond")
public final class CondFormula extends Formula {
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
    try {
      t = condition.profile(test.executeBoolean(frame));
    }
    catch (UnexpectedResultException e) {
      throw new Bail();
    }
    if ( t ) {
      return yes.execute(frame);
    }
    else {
      return no.execute(frame);
    }
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(6), new Cell(test.toNoun(), new Cell(yes.toNoun(), no.toNoun())));
  }
}
