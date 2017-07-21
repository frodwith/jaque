package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;

public abstract class PairGateNode extends SampleNode {
  protected abstract Object executePair(Object a, Object b);
  
  @Override
  public Object doSample(Object sample) {
    Cell c = Cell.expect(sample);
    return executePair(c.head, c.tail);
  }
}
