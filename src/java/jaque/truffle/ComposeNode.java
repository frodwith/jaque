package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

public final class ComposeNode extends Formula {
  public final Formula f;
  public final Formula g;

  public ComposeNode(Formula f, Formula g) {
    this.f = f;
    this.g = g;
  }

  public Result apply(Environment e) {
    Result r = f.apply(e);
    return g.apply(new Environment(r.m, r.r));
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(7), new Cell(f.toNoun(), g.toNoun()));
  }
}
