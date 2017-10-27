package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class QuoteLongNode extends OpNode {
  private final long value;
  
  public QuoteLongNode(long value) {
    this.value = value;
  }

  @Override
  public void execute(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    s.pop();
    s.push(value);
  }

}
