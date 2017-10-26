package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.truffle.Context;

public final class CallNode extends FlowNode {
  private final Axis axis;
  private final Context context;
  @Child private CallOpNode opNode;
  
  public CallNode(Context context, Axis axis) {
    this.context = context;
    this.axis = axis;
  }

  @Override
  public Continuation execute(VirtualFrame frame) {
    return opNode.executeCall(frame, context, this.continuation, getStack(frame).peek(), axis);
  }

}
