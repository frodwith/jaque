package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.Node.Child;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.dsl.Specialization;

@NodeInfo(shortName = "bump")
public abstract class BumpFormula extends Formula {
  @Child private Formula f;

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long bump(long v) {
    return Math.incrementExact(v);
  }

  @Specialization
  protected Atom bump(Atom v) {
    return v.bump();
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(4), f.toNoun());
  }
}
