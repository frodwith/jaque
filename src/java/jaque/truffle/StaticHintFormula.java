package jaque.truffle;

import jaque.interpreter.Hint;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "static-hint")
public final class StaticHintFormula extends Formula {
  @Child private Formula f;
  private final Atom kind;

  public StaticHintFormula(Atom kind, Formula f) {
    this.kind = kind;
    this.f = f;
  }
  
  public Object execute(VirtualFrame frame) {
    NockContext c = getContext(frame);
    Hint h = new Hint(kind, 0, getSubject(frame), f.source());
    Object product = c.startHint(h);
    if ( null == product) {
      product = f.executeSafe(frame);
      c.endHint(h, product);
    }
    return product;
  }

  public Cell toCell() {
    return new Cell(10, new Cell(this.kind, f.toCell()));
  }
}
