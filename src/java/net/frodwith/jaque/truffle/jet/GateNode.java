package net.frodwith.jaque.truffle.jet;

import java.util.Deque;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.bloc.Continuation;

public final class GateNode extends ImplementationNode {
  @Child private UnaryOpNode op;
  
  public GateNode(UnaryOpNode op) {
    this.op = op;
  }

  @Override
  public Continuation executeJet(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    Object subject  = s.pop();
    try {
      Object payload  = Cell.expect(subject).tail;
      Object sample   = Cell.expect(payload).head;
      Object product  = op.executeUnary(frame, sample);
      s.push(product);
      return Continuation.ret();
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }
}
