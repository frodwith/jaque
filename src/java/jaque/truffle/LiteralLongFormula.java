package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class LiteralLongFormula extends Formula {
  public final long value;

  public LiteralLongFormula(long value) {
    this.value = value;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return value;
  }

  @Override
  public long executeLong(VirtualFrame frame) {
    return value;
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(1), Atom.fromLong(value));
  }
}
