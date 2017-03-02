package jaque.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;

import jaque.interpreter.Hint;
import jaque.noun.Atom;
import jaque.noun.Cell;
import jaque.noun.Noun;

public final class DynamicHintFormula extends Formula {
  @Child private Formula hintF;
  @Child private Formula nextF;
  private final Atom kind;

  public DynamicHintFormula(Atom kind, Formula hintF, Formula nextF) {
    this.kind  = kind;
    this.hintF = hintF;
    this.nextF = nextF;
  }
  
  public Object execute(VirtualFrame frame) {
    NockContext c = getContext(frame);
    Hint h = new Hint(kind, hintF.execute(frame), getSubject(frame), nextF.source());
    Object product = c.startHint(h);
    if ( null == product ) {
      product = nextF.execute(frame);
      c.endHint(h, product);
    }
    return product;
  }

  @Override
  public Cell toCell() {
    return new Cell(10, new Cell(hintF.toCell(), nextF.toCell()));
  }
}
