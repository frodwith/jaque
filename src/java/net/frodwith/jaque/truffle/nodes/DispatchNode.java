package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import net.frodwith.jaque.truffle.TailException;

public abstract class DispatchNode extends JaqueNode {
  public abstract Object executeDispatch(VirtualFrame frame, CallTarget callTarget, Object subject);
  
  @Specialization(limit  = "3",
                  guards = { "callTarget == cachedTarget" })
  public Object doDirect(VirtualFrame frame, CallTarget callTarget, Object subject, 
      @Cached("callTarget") CallTarget cachedTarget,
      @Cached("create(callTarget)") DirectCallNode callNode) {
    return callNode.call(frame, new Object[] { subject });
  }
  
  @Specialization( replaces = "doDirect" )
  public Object doIndirect(VirtualFrame frame, CallTarget callTarget, Object subject,
      @Cached("create()") IndirectCallNode callNode) {
    return callNode.call(frame, callTarget, new Object[] { subject });
  }
  
  public Object call(VirtualFrame frame, CallTarget target, Object subject) {
    while ( true ) {
      try {
        return executeDispatch(frame, target, subject);
      }
      catch ( TailException e ) {
        target = e.target;
        subject = e.subject;
      }
    }
  }
}
