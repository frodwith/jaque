package net.frodwith.jaque.truffle.bloc;

import java.util.Deque;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class SwapNode extends OpNode {
  @Override
  public void execute(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    Object a = s.pop();
    Object b = s.pop();
    s.push(a);
    s.push(b);
  }
}
