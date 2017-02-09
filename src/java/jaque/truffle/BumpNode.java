package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

@NodeInfo(shortName = "bump")
public final class BumpNode extends Formula {
  @Child private Formula f;

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long bump(long v) {
    return Math.incrementExact(v);
  }

  @Specialization
  protected Atom bump(Atom v) {
    return v.bump();
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(4), f.toNoun());
  }
}
