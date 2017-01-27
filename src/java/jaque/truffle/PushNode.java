package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

public final class PushNode extends Formula {
  public final Formula f;
  public final Formula g;

  public PushNode(Formula f, Formula g) {
    this.f = f;
    this.g = g;
  }

  public Result apply(Environment e) {
    Result r = f.apply(e);
    return g.apply(new Environment(r.m, new Cell(r.r, e.subject)));
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(8), new Cell(f.toNoun(), g.toNoun()));
  }
}
