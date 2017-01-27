package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

public final class NockNode extends Formula {
  public final Formula subjectF;
  public final Formula formulaF;

  public NockNode(Formula subjectF, Formula formulaF) {
    this.subjectF = subjectF;
    this.formulaF = formulaF;
  }

  public Result apply(Environment e) {
    Result  subjectR = subjectF.apply(e);
    Result  formulaR = formulaF.apply(new Environment(subjectR.m, e.subject));
    Noun    formulaN = formulaR.r;
    if ( !(formulaN instanceof Cell) ) {
      throw new Bail();
    }
    else {
      Formula cont = Formula.fromNoun((Cell) formulaN);
      return cont.apply(new Environment(formulaR.m, subjectR.r));
    }
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(2), new Cell(subjectF.toNoun(), formulaF.toNoun()));
  }
}
