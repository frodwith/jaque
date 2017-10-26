package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class TossNode extends OpNode {
  @Override
  public void execute(VirtualFrame frame) {
    getStack(frame).pop();
  }
}
