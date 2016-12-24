package jaque.interpreter;

import jaque.noun.*;

public interface Jet {
  public Object hotKey();
  public Result applyCore(Machine m, Cell core);
}
