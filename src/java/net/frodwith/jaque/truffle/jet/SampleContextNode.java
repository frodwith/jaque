package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public abstract class SampleContextNode extends PayloadNode {
  protected abstract Object doSampleContext(VirtualFrame frame, Object sample, Object context);

  @Override
  public Object doPayload(VirtualFrame frame, Object payload) {
    Cell c = Cell.orBail(payload);
    return doSampleContext(frame, c.head, c.tail);
  }

}
