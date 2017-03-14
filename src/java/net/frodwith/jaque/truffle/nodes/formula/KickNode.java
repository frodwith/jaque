package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Bail;
import net.frodwith.jaque.truffle.nodes.KickDispatchNode;
import net.frodwith.jaque.truffle.nodes.KickDispatchNodeGen;

public class KickNode extends JumpFormula {
  private final Object axis;
  @Child private Formula coreFormula;
  @Child private KickDispatchNode dispatchNode;
  
  public KickNode(Object axis, Formula coreFormula) {
    this.axis = axis;
    this.coreFormula = coreFormula;
    this.dispatchNode = KickDispatchNodeGen.create();
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Cell core;
    try {
      core = coreFormula.executeCell(frame);
      return dispatchNode.executeKick(core, axis);
    } 
    catch (UnexpectedResultException e) {
      throw new Bail();
    }
  }
}
