package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Bail;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TailException;

/* Not Binary only because binary are Safe, but otherwise similar */
public class NockNode extends JumpFormula {
  @Child private Formula fol;
  @Child private Formula sub;
  
  public NockNode(Formula fol, Formula sub) {
    this.fol = fol;
    this.sub = sub;
  }
  
  public Context getContext() {
    // TODO
    return null;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object subject = getSubject(frame);
    Cell formula;
    try {
      formula = fol.executeCell(frame);
    }
    catch (UnexpectedResultException e) {
      throw new Bail();
    }
    CallTarget target = getContext().getNock(formula);

    setSubject(frame, subject);
    throw new TailException(target, sub.executeSafe(frame));
  }

}
