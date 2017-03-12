package net.frodwith.jaque.truffle.driver;

public class AxisArm extends Specification {
  public final Object axis;

  public AxisArm(String label, Object axis, Driver driver) {
    super(label, driver);
    this.axis = axis;
  }
}
