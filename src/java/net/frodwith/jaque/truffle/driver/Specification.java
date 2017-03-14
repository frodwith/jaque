package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.truffle.nodes.jet.JetNode;

public abstract class Specification {
  public final String label;
  public final Class<? extends JetNode> jetClass;
  
  protected Specification(String label, Class<? extends JetNode> jetClass) {
    this.label = label;
    this.jetClass = jetClass;
  }
}
