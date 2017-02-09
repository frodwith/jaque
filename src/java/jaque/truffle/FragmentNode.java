package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "frag")
public final class FragmentNode extends Formula {
  public final Atom axis;

  public FragmentNode(Atom axis) {
    this.axis = axis;
  }

  public Object execute(VirtualFrame frame) {
    return Formula.fragment(axis, getSubject(frame));
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(0), axis);
  }
}
