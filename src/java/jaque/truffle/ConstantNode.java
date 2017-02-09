package jaque.truffle;

import jaque.noun.*;

@NodeInfo(shortName = "const")
public final class ConstantNode extends Formula {
  public final Noun value;

  public ConstantNode(Noun value) {
    this.value = value;
  }

  public Noun execute(VirtualFrame frame) {
    return value;
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(1), value);
  }
}
