package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ComposeNode extends JumpFormula {
  @Child private Formula f;
  @Child private Formula g;
  
  public ComposeNode(Formula f, Formula g) {
    this.f = f;
    this.g = g;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    setSubject(frame, f.executeSafe(frame));
    return g.executeGeneric(frame);
  }
}
