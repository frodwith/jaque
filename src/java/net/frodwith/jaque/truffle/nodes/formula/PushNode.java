package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public abstract class PushNode extends ChangeFormula {
  @Override
  public Object executeSubject(VirtualFrame frame, Object subject) {
    return getG().executeSubject(frame,
      new Cell(getF().executeSafe(frame, subject), subject));
  }
}
