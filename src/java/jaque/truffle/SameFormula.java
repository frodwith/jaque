package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "same")
@NodeChildren({
  @NodeChild(value = "a", type = Formula.class),
  @NodeChild(value = "b", type = Formula.class)})
public abstract class SameFormula extends SafeFormula {
  public abstract Formula getA();
  public abstract Formula getB();
  
  protected abstract boolean executeSame(VirtualFrame frame, Object a, Object b);

  @Specialization
  protected boolean same(long a, long b) {
    return a == b;
  }
  
  @Specialization
  protected boolean same(Atom a, Atom b) {
    return a.equals(b);
  }

  @Specialization
  protected boolean same(Cell a, Cell b) {
    return a.equals(b);
  }

  @Specialization(guards = {"a.getClass() != b.getClass()"})
  protected boolean same(Object a, Object b) {
    return false;
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    return executeBoolean(frame);
  }
  
  @Override
  public boolean executeBoolean(VirtualFrame frame) {
    Object subject = getSubject(frame);
    Object a = getA().executeSafe(frame);
    setSubject(frame, subject);
    Object b = getB().executeSafe(frame);
    return executeSame(frame, a, b);
  }

  public Cell toCell() {
    return new Cell(5L, new Cell(getA().toCell(), getB().toCell()));
  }
}
