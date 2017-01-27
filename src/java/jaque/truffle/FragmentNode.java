package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

public final class FragmentNode extends Formula {
  public final Atom axis;

  public FragmentNode(Atom axis) {
    this.axis = axis;
  }

  public Result apply(Environment e) {
    Noun part = Interpreter.fragment(axis, e.subject);
    if ( null == part ) {
      throw new Bail();
    }
    else {
      return new Result(e.machine, part);
    }
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(0), axis);
  }
}
