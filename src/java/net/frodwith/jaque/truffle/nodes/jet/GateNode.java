package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.truffle.nodes.FragmentationNode;

public abstract class GateNode extends ImplementationNode {
  protected abstract Object doGate(Object atom);
  @Child private FragmentationNode fragment = new FragmentationNode(6L);

  @Override
  public Object doJet(Object subject) {
    return this.doGate(fragment.executeFragment(subject));
  }

}
