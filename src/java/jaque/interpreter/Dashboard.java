package jaque.interpreter;

import jaque.noun.*;

public interface Dashboard {
  public Dashboard install(Jet j);
  public Dashboard declare(Cell core, Noun clue);
  public Jet       find(Cell core, Atom axis);
  public Hook      hook(Cell core, String name);
}
