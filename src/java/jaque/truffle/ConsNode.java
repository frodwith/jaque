package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

public final class ConsNode extends Formula {
  public final Formula headF;
  public final Formula tailF;

  public ConsNode(Formula headF, Formula tailF) {
    this.headF = headF;
    this.tailF = tailF;
  }

  public Result apply(Environment e) {
    Result headR = headF.apply(e);
    Result tailR = tailF.apply(new Environment(headR.m, e.subject));

    return new Result(tailR.m, new Cell(headR.r, tailR.r));
  }

  public Cell toNoun() {
    return new Cell(headF.toNoun(), tailF.toNoun());
  }
}
