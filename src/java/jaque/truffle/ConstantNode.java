package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

public final class ConstantNode extends Formula {
  public final Noun value;

  public ConstantNode(Noun value) {
    this.value = value;
  }

  public Result apply(Environment e) {
    return new Result(e.machine, value);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(1), value);
  }
}
