package net.frodwith.jaque.truffle.bloc;

import java.util.Deque;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class DupNode extends OpNode {

  @Override
  public void execute(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    s.push(s.peek());
  }

}
