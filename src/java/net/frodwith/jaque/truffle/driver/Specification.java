package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public abstract class Specification {
  public final String label;
  public final Class<? extends ImplementationNode> jetClass;
  
  protected Specification(String label, Class<? extends ImplementationNode> jetClass) {
    this.label = label;
    this.jetClass = jetClass;
  }
}
