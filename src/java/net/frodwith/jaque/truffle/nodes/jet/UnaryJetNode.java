package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;

public abstract class UnaryJetNode extends ImplementationNode {
  protected abstract Object executeUnary(Object atom);
  @Child private FragmentationNode fragment = new FragmentationNode(6L);

  @Override
  public Object doJet(Object subject) {
    Cell core = TypesGen.asCell(subject);
    return this.executeUnary(fragment.executeFragment(core));
  }

}
