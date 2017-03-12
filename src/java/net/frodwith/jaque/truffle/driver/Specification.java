package net.frodwith.jaque.truffle.driver;

public abstract class Specification {
  public final String label;
  public final Driver driver;
  
  protected Specification(String label, Driver driver) {
    this.label = label;
    this.driver = driver;
  }
}
