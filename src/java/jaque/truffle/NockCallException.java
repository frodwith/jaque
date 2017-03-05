package jaque.truffle;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.ControlFlowException;

public class NockCallException extends ControlFlowException {
  public final CallTarget target;
  public final Object subject;

  public NockCallException(CallTarget target, Object subject) {
    this.target = target;
    this.subject = subject;
  }
}
