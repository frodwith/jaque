package net.frodwith.jaque.truffle.driver;

public class NamedArm extends Specification {
  public final String name;

  public NamedArm(String label, String name, Driver driver) {
    super(label, driver);
    this.name = name;
  }
}
