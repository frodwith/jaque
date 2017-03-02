package jaque.truffle;

import com.oracle.truffle.api.nodes.NodeInfo;

import jaque.noun.Atom;
import jaque.noun.Cell;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

@NodeInfo(shortName = "bump")
@NodeChild("f")
public abstract class BumpFormula extends Formula {
  public abstract Formula getF();

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long bump(long v) {
    return Math.incrementExact(v);
  }

  @Specialization
  protected Atom bump(Atom v) {
    return v.bump();
  }

  public Cell toCell() {
    return new Cell(4, getF().toCell());
  }
}
