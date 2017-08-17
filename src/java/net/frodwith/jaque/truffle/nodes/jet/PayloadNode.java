package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public abstract class PayloadNode extends ImplementationNode {
  protected abstract Object doPayload(VirtualFrame frame, Object payload);

  @Override
  public Object doJet(VirtualFrame frame, Object subject) {
    return doPayload(frame, Cell.expect(subject).tail);
  }

}
