package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.KickException;
import net.frodwith.jaque.truffle.NockException;
import net.frodwith.jaque.truffle.nodes.JumpNode;
import net.frodwith.jaque.truffle.nodes.JumpNodeGen;

public abstract class JumpFormula extends Formula {
  @Child private JumpNode jump = JumpNodeGen.create();
  
  private Context getContext() {
    // TODO
    return null;
  }
  
  @Override
  public Object executeSafe(VirtualFrame frame) {
    CallTarget target;
    Object subject;
    try {
      return executeGeneric(frame);
    }
    catch (KickException k) {
      target  = getContext().getKick(k.core, k.axis);
      subject = k.core;
    }
    catch (NockException n) {
      target  = getContext().getNock(n.formula);
      subject = n.subject;
    }
    return jump.executeJump(frame, target, subject);
  }
}
