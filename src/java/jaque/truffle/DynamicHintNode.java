package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

public final class DynamicHintNode extends HintNode {
  @Child private Formula hintF;
  @Child private Formula nextF;

  public DynamicHintNode(Atom kind, Formula hintF, Formula nextF) {
    super(kind);
    this.hintF = hintF;
    this.nextF = nextF;
  }

  public Noun clue(VirtualFrame frame) {
    return hintF.executeNoun(frame);
  }

  public Cell rawNext() {
    return nextF.toNoun();
  }

  public Formula next() {
    return nextF;
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(10), new Cell(hintF.toNoun(), nextF.toNoun()));
  }
}
