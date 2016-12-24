package jaque.interpreter;

import jaque.noun.*;

public final class Hook {
  public final Cell core;
  public final Cell formula;

  public Hook(Cell core, Cell formula) {
    this.core    = core;
    this.formula = formula;
  }
}
