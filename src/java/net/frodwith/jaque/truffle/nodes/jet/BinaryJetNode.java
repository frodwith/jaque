package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.TypesGen;

public abstract class BinaryJetNode extends ImplementationNode {
  protected abstract Object executeBinary(Object a, Object b);

  @Override
  public Object doJet(Object subject) {
    Cell core = TypesGen.asCell(subject);
    Cell sample = TypesGen.asCell(sampler.fragment(core));
    return this.executeBinary(sample.head, sample.tail);
  }

}
