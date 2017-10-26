package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class DeepNode extends OpNode {
  @Child private DeepOpNode deep = DeepOpNodeGen.create();

  @Override
  public void execute(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    s.push(deep.executeDeep(frame, s.pop()));
  }

}
