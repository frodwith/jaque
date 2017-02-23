package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
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

  public Cell toNoun() {
    return new Cell(Atom.fromLong(4), getF().toNoun());
  }
}
