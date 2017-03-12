package net.frodwith.jaque.truffle.driver;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;

public abstract class UnaryDriver implements Driver {
  public abstract Object applyUnary(Object argument);

  @Override
  public Object apply(Cell core) {
    return applyUnary(Noun.fragment(core, 6L));
  }

}
