package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.Context;

public final class EvalNode extends FlowNode {
  private Context context;
  @Child private EvalOpNode op = EvalOpNodeGen.create();

  public EvalNode(Context context) {
    this.context = context;
  }

  public Continuation execute(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    Object formula = s.pop();
    CallTarget t = op.executeTarget(frame, context, formula);
    return Continuation.call(t, after);
  }
}
