package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

@NodeInfo(shortName = "nock")
public final class NockNode extends Formula {
  @Child private Formula subject;
  @Child private Formula formula;
  @Child private NockDispatchNode dispatch;

  public NockNode(Formula subject, Formula formula) {
    this.subjectF = subjectF;
    this.formulaF = formulaF;
    this.dispatch = NockDispatchNodeGen.create();
  }

  @Specialization
  public Noun nock(VirtualFrame frame, Noun subject, Cell formula) {
    return dispatch.executeNock(frame, subject, formula);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(2), new Cell(subject.toNoun(), formula.toNoun()));
  }
}
