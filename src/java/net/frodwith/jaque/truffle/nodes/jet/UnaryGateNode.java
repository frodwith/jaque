package net.frodwith.jaque.truffle.nodes.jet;

public abstract class UnaryGateNode extends GateNode {
  protected abstract Object executeUnary(Object a);
  
  @Override
  public Object doGate(Object subject) {
    return executeUnary(subject);
  }
}
