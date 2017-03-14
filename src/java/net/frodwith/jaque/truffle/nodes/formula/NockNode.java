package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TailException;

/* Not Binary only because binary are Safe, but otherwise similar */
public class NockNode extends JumpFormula {
  @Child private Formula sub;
  @Child private Formula fol;
  private final Context context;
  
  public NockNode(Context context, Formula sub, Formula fol) {
    this.sub = sub;
    this.fol = fol;
    this.context = context;
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
    CallTarget target = context.getNock(formula);

    setSubject(frame, subject);
    throw new TailException(target, sub.executeSafe(frame));
  }

}
