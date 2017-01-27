package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

public final class DynamicHintNode extends HintNode {
  public final Atom    kind;
  public final Formula hintF;
  public final Formula nextF;

  public DynamicHintNode(Atom kind, Formula hintF, Formula nextF) {
    this.kind  = kind;
    this.hintF = hintF;
    this.nextF = nextF;
  }

  public Result clue(Environment e) {
    return hintF.apply(e);
  }

  public Cell rawNext() {
    return nextF.toNoun();
  }

  public Atom kind() {
    return kind;
  }

  public Formula next() {
    return nextF;
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(10), new Cell(hintF.toNoun(), nextF.toNoun()));
  }
}
