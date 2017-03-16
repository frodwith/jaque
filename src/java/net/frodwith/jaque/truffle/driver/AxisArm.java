package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public class AxisArm extends Arm {
  public final Object axis;

  public AxisArm(String label, Object axis, Class<? extends ImplementationNode> driver) {
    super(label, driver);
    this.axis = axis;
  }
}
