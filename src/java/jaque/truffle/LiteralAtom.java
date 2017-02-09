package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class LiteralAtom extends Formula {
  public final Atom value;

  public LiteralAtom(Noun value) {
    this.value = value;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return value;
  }

  @Override
  public Atom executeAtom(VirtualFrame frame) {
    return value;
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(1), value);
  }
}
