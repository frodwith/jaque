package jaque.interpreter;

import jaque.noun.Noun;

public interface Jet {
  public Result apply(Machine m, Noun subject);
}
