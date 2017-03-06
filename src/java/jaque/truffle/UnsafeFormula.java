package jaque.truffle;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;

import jaque.noun.Cell;

// formulae which may throw JumpExceptions
public abstract class UnsafeFormula extends Formula {
  @Child private NockJumpNode jump = NockJumpNodeGen.create();
//  @Child private IndirectCallNode callNode = IndirectCallNode.create();

  @Override
  public Object executeSafe(VirtualFrame frame) {
    NockContext c = getContext(frame);
    try {
      return execute(frame);
    }
    catch (NockCallException call) {
//      return callNode.call(frame, call.target, new Object[] { getContext(frame), call.subject });
      return jump.executeJump(frame, call.target, call.subject);
    }
  }
}
