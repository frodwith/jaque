package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Bail;
import net.frodwith.jaque.truffle.NockException;

/* Not Binary only because binary are Safe, but otherwise similar */
public class NockNode extends JumpFormula {
  @Child private Formula fol;
  @Child private Formula sub;
  
  public NockNode(Formula fol, Formula sub) {
    this.fol = fol;
    this.sub = sub;
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
    setSubject(frame, subject);
    Object newSub = sub.executeSafe(frame);

    throw new NockException(formula, newSub);
  }

}
