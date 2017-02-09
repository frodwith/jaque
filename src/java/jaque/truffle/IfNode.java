package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

@NodeInfo(shortName = "if")
public final class IfNode extends Formula {
  @Child private Formula test;
  @Child private Formula yes;
  @Child private Formula no;

  private final ConditionProfile condition = ConditionProfile.createCountingProfile();

  public Noun execute(VirtualFrame frame) {
    if ( condition.profile(test.executeBoolean(frame)) ) {
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