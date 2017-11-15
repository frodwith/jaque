package net.frodwith.jaque.truffle.jet;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.truffle.bloc.Continuation;

public final class TrelGateNode extends ImplementationNode {
  @Child private TernaryOpNode op;
  
  public TrelGateNode(TernaryOpNode op) {
    this.op = op;
  }

  @Override
  public Continuation executeJet(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    Object subject  = s.pop();
    try {
      Object payload  = Cell.expect(subject).tail;
      Trel   sample   = Trel.expect(Cell.expect(payload).head);
      Object product  = op.executeTernary(frame, sample.p, sample.q, sample.r);
      s.push(product);
      return Continuation.ret();
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }
}
