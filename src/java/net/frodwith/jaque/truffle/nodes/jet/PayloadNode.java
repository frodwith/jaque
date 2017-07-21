package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;

public abstract class PayloadNode extends ImplementationNode {
  protected abstract Object doPayload(Object payload);

  @Override
  public Object doJet(Object subject) {
    return doPayload(Cell.expect(subject).tail);
  }

}
