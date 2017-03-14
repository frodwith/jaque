package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.truffle.nodes.jet.JetNode;

public class AxisArm extends Specification {
  public final Object axis;

  public AxisArm(String label, Object axis, Class<? extends JetNode> jetClass) {
    super(label, jetClass);
    this.axis = axis;
  }
}
