package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;

public abstract class BinaryJetNode extends ImplementationNode {
  protected abstract Object executeBinary(Object a, Object b);
  @Child private FragmentationNode fragment = new FragmentationNode(6L);

  @Override
  public Object doJet(Object subject) {
    Cell core = TypesGen.asCell(subject);
    Cell sample = TypesGen.asCell(fragment.executeFragment(core));
    return this.executeBinary(sample.head, sample.tail);
  }

}
