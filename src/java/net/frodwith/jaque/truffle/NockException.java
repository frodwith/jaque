package net.frodwith.jaque.truffle;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.ControlFlowException;

import net.frodwith.jaque.data.Cell;

public class NockException extends ControlFlowException {
  public Cell formula;
  public Object subject;
  
  public NockException(Cell formula, Object subject) {
    this.formula = formula;
    this.subject = subject;
  }
}
