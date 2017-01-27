package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

public final class IfNode extends Formula {
  public final Formula testF;
  public final Formula yesF;
  public final Formula noF;

  public IfNode(Formula testF, Formula yesF, Formula noF) {
    this.testF = testF;
    this.yesF  = yesF;
    this.noF   = noF;
  }

  public Result apply(Environment e) {
    Result  testR = testF.apply(e);
    Formula cont;

    if ( testR.r.equals(Atom.YES) ) {
      cont = yesF;
    }
    else if ( testR.r.equals(Atom.NO) ) {
      cont = noF;
    }
    else {
      throw new Bail();
    }
  
    return cont.apply(new Environment(testR.m, e.subject));
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(6), new Cell(testF.toNoun(), new Cell(yesF.toNoun(), noF.toNoun())));
  }
}
