package net.frodwith.jaque.truffle.bloc;

import java.util.Deque;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class DeepNode extends OpNode {
  @Child private DeepOpNode deep;
  
  public DeepNode() {
    this.deep = DeepOpNodeGen.create();
    insert(deep);
  }

  @Override
  public void execute(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    s.push(deep.executeDeep(frame, s.pop()));
  }

}
