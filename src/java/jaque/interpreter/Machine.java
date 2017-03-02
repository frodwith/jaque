package jaque.interpreter;

import jaque.noun.*;

public interface Machine {
  public Result escape(Noun ref, Noun sam);
  public boolean fine(Cell core);
  public Jet find(Cell core, Atom axis);

  public Result startHint(Hint h);
  public Machine endHint(Hint h, Object product);
  public Machine declare(Cell core, Object clue);
}
