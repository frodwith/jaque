package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

public final class SameNode extends Formula {
  public final Formula headF;
  public final Formula tailF;

  public SameNode(Formula headF, Formula tailF) {
    this.headF = headF;
    this.tailF = tailF;
  }

  public Result apply(Environment e) {
    Result headR = headF.apply(e);
    Result tailR = tailF.apply(new Environment(headR.m, e.subject));
    Atom   pro   = headR.r.equals(tailR.r) ? Atom.YES : Atom.NO;

    return new Result(tailR.m, pro);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(5), new Cell(headF.toNoun(), tailF.toNoun()));
  }
}
