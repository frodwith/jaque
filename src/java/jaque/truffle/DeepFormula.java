package jaque.truffle;

import com.oracle.truffle.api.nodes.NodeInfo;

import jaque.noun.Atom;
import jaque.noun.Cell;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

@NodeInfo(shortName = "deep")
@NodeChild("f")
public abstract class DeepFormula extends Formula {
  public abstract Formula getF();

  @Specialization
  protected boolean deep(Atom v) {
    return false;
  }

  @Specialization
  protected boolean deep(Cell v) {
    return true;
  }

  @Specialization
  protected boolean deep(Object v) {
    return (v instanceof Cell);
  }

  public Cell toCell() {
    return new Cell(3, getF().toCell());
  }
}
