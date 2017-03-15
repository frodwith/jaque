package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import net.frodwith.jaque.truffle.TailException;

public abstract class DispatchNode extends JaqueNode {
  public abstract Object executeDispatch(VirtualFrame frame, CallTarget callTarget, Object subject);
  
  @Specialization(limit  = "3", // this is the default, but may be worth tuning for performance
                  guards = { "callTarget == cachedTarget" })
  public Object doDirect(VirtualFrame frame, CallTarget callTarget, Object subject, 
      @Cached("callTarget") CallTarget cachedTarget,
      @Cached("create(callTarget)") DirectCallNode callNode) {
    try {
      return callNode.call(frame, new Object[] { subject });
    }
    catch (TailException e) {
      return executeDispatch(frame, e.target, e.subject);
    }
  }
  
  /* This should get hit when you have a single call site dispatching a lot of
  * tail calls to different call targets - sort of a pathological case.
   */
  @Specialization( replaces = "doDirect" )
  public Object doIndirect(VirtualFrame frame, CallTarget callTarget, Object subject,
      @Cached("create()") IndirectCallNode callNode) {
    while ( true ) {
      try {
        return callNode.call(frame, callTarget, new Object[] { subject });
      }
      catch (TailException e) {
        callTarget = e.target;
        subject = e.subject;
      }
    }
  }
}
