package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "same")
@NodeChildren({@NodeChild("a"), @NodeChild("b")})
public abstract class SameFormula extends Formula {
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
    return executeSame(frame, getA().executeSafe(frame), getB().executeSafe(frame));
  }

  public Cell toCell() {
    return new Cell(5, new Cell(getA().toCell(), getB().toCell()));
  }
}
