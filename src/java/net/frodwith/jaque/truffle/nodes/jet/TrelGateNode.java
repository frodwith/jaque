package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public abstract class TrelGateNode extends SampleNode {
  protected abstract Object executeTrel(VirtualFrame frame, Object a, Object b, Object c);
  
  @Override
  public Object doSample(VirtualFrame frame, Object sample) {
    Cell trel = Cell.orBail(sample),
         pair = Cell.orBail(trel.tail);
    return executeTrel(frame, trel.head, pair.head, pair.tail);
  }
}
