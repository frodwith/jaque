package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public abstract class PairGateNode extends SampleNode {
  protected abstract Object executePair(VirtualFrame frame, Object a, Object b);
  
  @Override
  public Object doSample(VirtualFrame frame, Object sample) {
    Cell c = Cell.orBail(sample);
    return executePair(frame, c.head, c.tail);
  }
}
