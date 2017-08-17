package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public abstract class SampleNode extends PayloadNode {
  protected abstract Object doSample(VirtualFrame frame, Object sample);

  @Override
  public Object doPayload(VirtualFrame frame, Object payload) {
    return doSample(frame, Cell.expect(payload).head);
  }

}
