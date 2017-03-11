package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;
import net.frodwith.jaque.data.Cell;
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
  public Object executeSubject(VirtualFrame frame, Object subject) {
    Cell formula  = fol.executeCell(frame, subject);
    Object newSub = sub.executeSafe(frame, subject);

    throw new NockException(formula, newSub);
  }

}
