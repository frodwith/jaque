package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

@NodeInfo(shortName = "frag")
public final class FragmentNode extends Formula {
  public final Atom axis;

  public FragmentNode(Atom axis) {
    this.axis = axis;
  }

  public Noun execute(VirtualFrame frame) {
    return Formula.fragment(axis, frame.getArguments()[0]);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(0), axis);
  }
}
