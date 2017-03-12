package net.frodwith.jaque.truffle;

import com.oracle.truffle.api.nodes.ControlFlowException;

import net.frodwith.jaque.data.Cell;

public class KickException extends ControlFlowException {
  public Cell core;
  public Object axis;
  
  public KickException(Cell core, Object axis) {
    this.core = core;
    this.axis = axis;
  }
}
