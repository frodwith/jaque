package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class SafeFormula extends Formula {
  public Object doSafe(VirtualFrame frame) {
    return executeGeneric(frame);
  }
}
