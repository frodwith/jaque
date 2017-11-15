package net.frodwith.jaque.truffle.jet;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.bloc.Continuation;

public final class SampleContextNode extends ImplementationNode {
  @Child private BinaryOpNode op;
  
  public SampleContextNode(BinaryOpNode op) {
    this.op = op;
  }
  
  @Override
  public Continuation executeJet(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    Object subject  = s.pop();
    try {
      Cell payload   = Cell.expect(Cell.expect(subject).tail);
      Object product = op.executeBinary(frame, payload.head, payload.tail);
      s.push(product);
      return Continuation.ret();
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }
}