package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;

public class SameNode extends OpNode {
  @Child private SameOpNode same = SameOpNodeGen.create();

  @Override
  public void execute(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    Object a = s.pop();
    Object b = s.pop();
    s.push(same.executeSame(frame, a, b));
  }

}
