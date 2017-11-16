package net.frodwith.jaque.truffle.jet;

import java.util.Deque;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.bloc.Continuation;

public final class AesEcbNode extends ImplementationNode {
  @Child private BinaryOpNode op;
  
  public AesEcbNode(BinaryOpNode op) {
    this.op = op;
  }
  
  @Override
  public Continuation executeJet(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    Object subject  = s.pop();
    try {
      Cell payload   = Cell.expect(Cell.expect(subject).tail);
      Cell context   = Cell.expect(payload.tail);
      Object conSam  = Cell.expect(context.tail).head;
      Object product = op.executeBinary(frame, conSam, payload.head);
      s.push(product);
      return Continuation.ret();
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }
}