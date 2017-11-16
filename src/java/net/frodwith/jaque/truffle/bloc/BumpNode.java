package net.frodwith.jaque.truffle.bloc;

import java.util.Deque;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class BumpNode extends OpNode {
  @Child private BumpOpNode bump;
  
  public BumpNode() {
    this.bump = BumpOpNodeGen.create();
    insert(bump);
  }

  @Override
  public void execute(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    s.push(bump.executeBump(frame, s.pop()));
  }

}
