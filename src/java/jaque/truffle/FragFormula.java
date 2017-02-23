package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "frag")
public final class FragFormula extends Formula {
  public final Atom axis;

  public FragFormula(Atom axis) {
    this.axis = axis;
  }

  public Object execute(VirtualFrame frame) {
    return fragment(axis, getSubject(frame));
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(0), axis);
  }
}
