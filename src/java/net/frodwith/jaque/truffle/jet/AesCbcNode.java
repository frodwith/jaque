package net.frodwith.jaque.truffle.jet;

import java.util.Deque;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.bloc.Continuation;

public final class AesCbcNode extends ImplementationNode {
  @Child private TernaryOpNode op;
  
  public AesCbcNode(TernaryOpNode op) {
    this.op = op;
  }
  
  @Override
  public Continuation executeJet(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    Object subject  = s.pop();
    try {
      Cell payload   = Cell.expect(Cell.expect(subject).tail);
      Cell context   = Cell.expect(payload.tail);
      Cell conSam    = Cell.expect(Cell.expect(context.tail).head);
      Object product = op.executeTernary(frame, conSam.head, conSam.tail, payload.head);
      s.push(product);
      return Continuation.ret();
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }
}