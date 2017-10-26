package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;

public class BumpNode extends OpNode {
  @Child private BumpOpNode bump = BumpOpNodeGen.create();

  @Override
  public void execute(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    s.push(bump.executeBump(frame, s.pop()));
  }

}
