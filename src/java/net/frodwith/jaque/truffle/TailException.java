package net.frodwith.jaque.truffle;

import java.util.function.Supplier;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.ControlFlowException;

public class TailException extends ControlFlowException {
  public final CallTarget target;
  public final Object[] arguments;
  public final Supplier<String> doer;
  
  public TailException(CallTarget target, Object[] arguments, Supplier<String> doer) {
    this.target = target;
    this.arguments = arguments;
    this.doer = doer;
  }

  public TailException(CallTarget target, Object[] arguments) {
    this(target, arguments, null);
  }
}
