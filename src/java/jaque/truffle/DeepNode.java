package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

public final class DeepNode extends Formula {
  public final Formula f;

  public DeepNode(Formula f) {
    this.f = f;
  }

  public Result apply(Environment e) {
    Result r   = f.apply(e);
    return new Result(r.m, (r.r instanceof Cell ? Atom.YES : Atom.NO));
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(3), f.toNoun());
  }
}
