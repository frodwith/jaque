package net.frodwith.jaque.truffle.nodes.jet;

public abstract class GateNode extends SampleNode {
  protected abstract Object executeGate(Object a);
  
  @Override
  public Object doSample(Object sample) {
    return executeGate(sample);
  }
}
