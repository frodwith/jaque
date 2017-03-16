package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public class NamedArm extends Arm {
  public final String name;

  public NamedArm(String label, String name, Class<? extends ImplementationNode> driver) {
    super(label, driver);
    this.name = name;
  }
}
