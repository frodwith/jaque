package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class ComposeNode extends ChangeFormula {
  @Override
  public Object executeSubject(VirtualFrame frame, Object subject) {
    return getG().executeSubject(frame, getF().executeSafe(frame, subject));
  }
}
