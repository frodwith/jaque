package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public final class BlockNode extends BlocNode {
  @Children private final OpNode[] body;
  @Child private FlowNode flow;
  private final boolean hasFlow;
  
  public BlockNode(OpNode[] body, FlowNode flow) {
    this.body = body;
    this.flow = flow;
    this.hasFlow = (null != flow);
  }
  
  @ExplodeLoop // should really work
  public Continuation execute(VirtualFrame frame) {
    CompilerAsserts.compilationConstant(body.length);
    for ( OpNode node : body ) {
      node.execute(frame);
    }
    return hasFlow ? flow.execute(frame) : Continuation.ret();
  }
}
