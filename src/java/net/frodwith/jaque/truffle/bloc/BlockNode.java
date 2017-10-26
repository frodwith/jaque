package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public final class BlockNode extends BlocNode {
  @Children private final OpNode[] body;
  @Child private FlowNode flow;
  
  public BlockNode(OpNode[] body, FlowNode flow) {
    this.body = body;
    this.flow = flow;
  }
  
  @ExplodeLoop
  public Continuation execute(VirtualFrame frame) {
    for ( OpNode node : body ) {
      node.execute(frame);
    }
    
    return (null == flow) ? Continuation.ret() : flow.execute(frame);
  }
}
