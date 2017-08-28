package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.Location;
import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public abstract class Arm {
  public final String label;
  public final Class<? extends ImplementationNode> driver;

  public abstract boolean matches(Location loc, Object axis);
  
  protected Arm(String label, Class<? extends ImplementationNode> driver) {
    this.label = label;
    this.driver = driver;
  }

}
