package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.Definition;

public class AxisArm extends Arm {
  public final Object axis;

  public AxisArm(String label, Object axis, Class<? extends Definition> driver) {
    super(label, driver);
    this.axis = axis;
  }

  @Override
  public boolean matches(Location loc, Object axis) {
    return Atom.equals(this.axis, axis);
  }
}
