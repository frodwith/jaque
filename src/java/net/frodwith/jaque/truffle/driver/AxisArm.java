package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public class AxisArm extends Specification {
  public final Object axis;

  public AxisArm(String label, Object axis, Class<? extends ImplementationNode> jetClass) {
    super(label, jetClass);
    this.axis = axis;
  }
}
