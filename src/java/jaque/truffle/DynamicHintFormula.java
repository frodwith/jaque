package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class DynamicHintFormula extends HintFormula {
  @Child private Formula hintF;
  @Child private Formula nextF;

  public DynamicHintFormula(Atom kind, Formula hintF, Formula nextF) {
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
