package net.frodwith.jaque.truffle;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.ControlFlowException;

public class TailException extends ControlFlowException {
  public final CallTarget target;
  public final Object[] arguments;
  
  public TailException(CallTarget target, Object[] arguments) {
    this.target = target;
    this.arguments = arguments;
  }
}
