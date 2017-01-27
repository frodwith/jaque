package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

public final class EscapeNode extends Formula {
  public final Formula gateF;
  public final Formula sampleF;

  public EscapeNode(Formula gateF, Formula sampleF) {
    this.gateF   = gateF;
    this.sampleF = sampleF;
  }

  public Result apply(Environment e) {
    Result g = gateF.apply(e);
    Result s = sampleF.apply(new Environment(g.m, e.subject));
    return s.m.escape(new Cell(g.r, s.r));
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(11), new Cell(gateF.toNoun(), sampleF.toNoun()));
  }
}
