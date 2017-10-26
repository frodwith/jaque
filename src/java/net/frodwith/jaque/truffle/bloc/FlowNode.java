package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class FlowNode extends BlocNode {
  /* if we weren't originally in tail position, there will be
     some code to run after the eval, i.e. a continuation.
     blok exists to arrange this. */
  @CompilationFinal protected CallTarget after = null;
  public abstract Continuation execute(VirtualFrame frame);
  public void setAfter(CallTarget target) {
    after = target;
  }
}
