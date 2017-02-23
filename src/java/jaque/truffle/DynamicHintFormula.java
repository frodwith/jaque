package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.Node.Child;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public final class DynamicHintFormula extends HintFormula {
  @Child private Formula hintF;
  @Child private Formula nextF;

  public DynamicHintFormula(Atom kind, Formula hintF, Formula nextF) {
    super(kind);
    this.hintF = hintF;
    this.nextF = nextF;
  }

  public Noun clue(VirtualFrame frame) {
    try {
    	return hintF.executeNoun(frame);
    } catch (UnexpectedResultException e) {
      throw new UnsupportedSpecializationException(this, new Node[] {hintF}, e);
    }
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
