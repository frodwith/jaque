package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

public final class StaticHintNode extends HintNode {
  public final Atom    kind;
  public final Formula f;

  public StaticHintNode(Atom kind, Formula f) {
    this.kind = kind;
    this.f    = f;
  }

  public Result clue(Environment e) {
    return new Result(e.machine, Atom.ZERO);
  }

  public Cell rawNext() {
    return f.toNoun();
  }
  
  public Atom kind() {
    return kind;
  }

  public Formula next() {
    return f;
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(10), new Cell(kind, f.toNoun()));
  }
}
