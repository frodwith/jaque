package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;

public abstract class TrelGateNode extends SampleNode {
  protected abstract Object executeTrel(Object a, Object b, Object c);
  
  @Override
  public Object doSample(Object sample) {
    Cell trel = Cell.expect(sample),
         pair = Cell.expect(trel.tail);
    return executeTrel(trel.head, pair.head, pair.tail);
  }
}
