package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class GateNode extends SampleNode {
  protected abstract Object executeGate(VirtualFrame frame, Object a);
  
  @Override
  public Object doSample(VirtualFrame frame, Object sample) {
    return executeGate(frame, sample);
  }
}
