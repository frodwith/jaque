package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.TypesGen;

public abstract class UnaryJetNode extends ImplementationNode {
  protected abstract Object executeUnary(Object atom);

  @Override
  public Object doJet(Object subject) {
    Cell core = TypesGen.asCell(subject);
    return this.executeUnary(sampler.fragment(core));
  }

}