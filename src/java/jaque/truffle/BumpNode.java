package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

public final class BumpNode extends Formula {
  public final Formula f;

  public BumpNode(Formula f) {
    this.f = f;
  }

  public Result apply(Environment e) {
    Result r = f.apply(e);
    if ( !(r.r instanceof Atom) ) {
      throw new Bail();
    }
    else {
      return new Result(r.m, ((Atom) r.r).bump());
    }
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(4), f.toNoun());
  }
}
