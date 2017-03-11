package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeChild(value = "sub", type = Formula.class)
public abstract class UnaryFormula extends SafeFormula {
  protected abstract Formula getSub();
  protected abstract Object executeUnary(VirtualFrame frame, Object argument);

  @Override
  public Object executeSubject(VirtualFrame frame, Object subject) {
    return executeUnary(frame, getSub().executeSafe(frame, subject));
  }
}
