package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;

public abstract class SampleNode extends PayloadNode {
  protected abstract Object doSample(Object sample);

  @Override
  public Object doPayload(Object payload) {
    return doSample(Cell.expect(payload).head);
  }

}
