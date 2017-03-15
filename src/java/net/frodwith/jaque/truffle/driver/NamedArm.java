package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public class NamedArm extends Specification {
  public final String name;

  public NamedArm(String label, String name, Class<? extends ImplementationNode> jetClass) {
    super(label, jetClass);
    this.name = name;
  }
}
