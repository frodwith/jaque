package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

public final class KickNode extends Formula {
  public final Atom    axis;
  public final Formula coreF;

  public KickNode(Atom axis, Formula coreF) {
    this.axis  = axis;
    this.coreF = coreF;
  }

  public Result apply(Environment e) {
    Result r   = coreF.apply(e);
    if ( !(r.r instanceof Cell) ) {
      throw new Bail();
    }
    else {
      Cell cor = (Cell) r.r;
      Jet  j   = r.m.dashboard().find(cor, axis);
      if ( null == j ) {
        Noun n = Interpreter.fragment(axis, cor);
        if ( !(n instanceof Cell) ) {
          throw new Bail();
        }
        else {
          Formula f = Formula.fromNoun((Cell) n);
          return f.apply(new Environment(r.m, cor));
        }
      }
      else {
        return j.applyCore(r.m, cor);
      }
    }
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(9), new Cell(axis, coreF.toNoun()));
  }
}
