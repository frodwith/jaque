package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class LiteralCellFormula extends Formula {
  public final Cell value;

  public LiteralCellFormula(Cell value) {
    this.value = value;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return value;
  }

  @Override
  public Cell executeCell(VirtualFrame frame) {
    return value;
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(1), value);
  }
}
