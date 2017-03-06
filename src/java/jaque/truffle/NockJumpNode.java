package jaque.truffle;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;

public abstract class NockJumpNode extends NockNode {
  protected abstract Object executeJump(VirtualFrame frame, CallTarget target, Object subject);
  
  @Specialization(limit = "1",
                  guards = { "target == cachedTarget" })
  public Object jumpDirect(VirtualFrame frame, CallTarget target, Object subject,
    @Cached("target") CallTarget cachedTarget,
    @Cached("create(target)") DirectCallNode callNode)
  {
    return callNode.call(frame, makeArgs(frame, subject));
  }
  
  @Specialization(replaces = "jumpDirect")
  public Object jumpSlow(VirtualFrame frame, CallTarget target, Object subject,
    @Cached("create()") IndirectCallNode callNode)
  {
      return callNode.call(frame, target, makeArgs(frame, subject));
  }

  protected static Object[] makeArgs(VirtualFrame frame, Object subject) {
    return new Object[] { getContext(frame), subject };
  }
}
