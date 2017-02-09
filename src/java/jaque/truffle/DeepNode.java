package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

@NodeInfo(shortName = "3")
public abstract class DeepNode extends Formula {
  @Child private Formula f;

  @Specialization
  protected boolean deep(boolean v) {
    return false;
  }

  @Specialization
  protected boolean deep(long v) {
    return false;
  }

  @Specialization
  protected boolean deep(Atom v) {
    return false;
  }

  @Specialization
  protected boolean deep(Cell v) {
    return true;
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(3), f.toNoun());
  }
}
