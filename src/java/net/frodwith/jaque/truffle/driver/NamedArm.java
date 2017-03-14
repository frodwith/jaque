package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.truffle.nodes.jet.JetNode;

public class NamedArm extends Specification {
  public final String name;

  public NamedArm(String label, String name, Class<? extends JetNode> jetClass) {
    super(label, jetClass);
    this.name = name;
  }
}
