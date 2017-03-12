package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeChildren({
  @NodeChild(value = "left", type = Formula.class),
  @NodeChild(value = "right", type = Formula.class)})
public abstract class BinaryFormula extends SafeFormula {
  protected abstract Formula getLeft();
  protected abstract Formula getRight();
  protected abstract Object executeBinary(Object left, Object right);

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object subject = getSubject(frame);
    Object left = getLeft().executeSafe(frame);
    setSubject(frame, subject);
    Object right = getRight().executeSafe(frame);

    return executeBinary(left, right);
  }
}
