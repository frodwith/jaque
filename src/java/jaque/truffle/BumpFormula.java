package jaque.truffle;

import com.oracle.truffle.api.nodes.NodeInfo;

import jaque.noun.Atom;
import jaque.noun.Cell;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "bump")
@NodeChild(value = "f", type = Formula.class)
public abstract class BumpFormula extends SafeFormula {
  public abstract Formula getF();
  public abstract Object executeBump(VirtualFrame frame, Object atom);

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long bump(long v) {
    return Math.incrementExact(v);
  }

  @Specialization
  protected Atom bump(Atom v) {
    return v.bump();
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    return executeBump(frame, getF().executeSafe(frame));
  }

  public Cell toCell() {
    return new Cell(4L, getF().toCell());
  }
}
