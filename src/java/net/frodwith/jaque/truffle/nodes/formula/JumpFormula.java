package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.TailException;
import net.frodwith.jaque.truffle.nodes.JumpNode;
import net.frodwith.jaque.truffle.nodes.JumpNodeGen;

public abstract class JumpFormula extends Formula {
  @Child private JumpNode jump = JumpNodeGen.create();
  
  @Override
  public Object executeSafe(VirtualFrame frame) {
    try {
      return executeGeneric(frame);
    }
    catch (TailException n) {
      return jump.executeJump(frame, n.target, n.subject);
    }
  }
}
