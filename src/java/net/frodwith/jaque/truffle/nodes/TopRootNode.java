package net.frodwith.jaque.truffle.nodes;

import net.frodwith.jaque.truffle.NockLanguage;
import net.frodwith.jaque.truffle.TailException;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public class TopRootNode extends RootNode {
  @Child private DispatchNode dispatch;
  private final CallTarget target;
  
  public TopRootNode(CallTarget target) {
    super(NockLanguage.class, null, FunctionNode.DESCRIPTOR);
    this.dispatch = DispatchNodeGen.create();
    this.target = target;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return dispatch.call(frame, target, frame.getArguments());
  }

}
