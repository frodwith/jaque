package net.frodwith.jaque.truffle.jet;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.bloc.Continuation;

public final class PairGateNode extends ImplementationNode {
  @Child private BinaryOpNode op;
  
  public PairGateNode(BinaryOpNode op) {
    this.op = op;
  }
  
  @Override
  public Continuation executeJet(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    Object subject  = s.pop();
    try {
      Object payload  = Cell.expect(subject).tail;
      Cell   sample   = Cell.expect(Cell.expect(payload).head);
      Object product  = op.executeBinary(frame, sample.head, sample.tail);
      s.push(product);
      return Continuation.ret();
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }
}
