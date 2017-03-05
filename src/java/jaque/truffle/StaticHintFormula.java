package jaque.truffle;

import jaque.interpreter.Hint;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "static-hint")
public final class StaticHintFormula extends HintFormula {
  @Child private Formula f;
  private final Atom kind;

  public StaticHintFormula(Atom kind, Formula f) {
    super(f);
    this.kind = kind;
    this.f = f;
  }
  
  public Hint executeHint(VirtualFrame frame) {
    return new Hint(kind, Atom.ZERO, getSubject(frame), source());
  }
  
  public Object executeNext(VirtualFrame frame) {
    return f.executeSafe(frame);
  }
  
  public Cell toCell() {
    return new Cell(10L, new Cell(this.kind, f.toCell()));
  }
}
