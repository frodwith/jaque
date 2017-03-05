package jaque.truffle;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import jaque.noun.Atom;
import jaque.noun.Cell;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "deep")
@NodeChild(value = "f", type = Formula.class)
public abstract class DeepFormula extends SafeFormula {
  public abstract Formula getF();
  public abstract boolean executeDeep(VirtualFrame frame, Object o);

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
  
  @Override
  public Object execute(VirtualFrame frame) {
    return executeBoolean(frame);
  }
  
  @Override
  public boolean executeBoolean(VirtualFrame frame) {
    return executeDeep(frame, getF().executeSafe(frame));
  }

  public Cell toCell() {
    return new Cell(3L, getF().toCell());
  }
}
