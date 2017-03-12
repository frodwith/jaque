package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.data.Atom;

public class Decrement extends UnaryDriver {
  @Override
  public Object applyUnary(Object argument) {
    return Atom.decrement(argument);
  }
}
