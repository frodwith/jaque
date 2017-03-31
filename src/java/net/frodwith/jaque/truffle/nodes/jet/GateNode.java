package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.truffle.FragmentationException;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;

public abstract class GateNode extends ImplementationNode {
  protected abstract Object doGate(Object atom);
  @Child private FragmentationNode fragment = new FragmentationNode(6L);

  @Override
  public Object doJet(Object subject) {
    try {
      return this.doGate(fragment.executeFragment(subject));
    }
    catch ( FragmentationException e) {
      throw new Bail();
    }
  }

}
