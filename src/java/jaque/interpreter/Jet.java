package jaque.interpreter;

import jaque.noun.Atom;
import jaque.noun.Noun;

public interface Jet {
  public Result apply(Machine machine, Object[] arguments);
  public Atom[] argumentLocations();
}
