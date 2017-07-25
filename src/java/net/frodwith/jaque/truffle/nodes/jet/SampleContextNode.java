package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;

public abstract class SampleContextNode extends PayloadNode {
  protected abstract Object doSampleContext(Object sample, Object context);

  @Override
  public Object doPayload(Object payload) {
    Cell c = Cell.expect(payload);
    return doSampleContext(c.head, c.tail);
  }

}
