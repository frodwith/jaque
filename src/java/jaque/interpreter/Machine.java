package jaque.interpreter;

import jaque.noun.*;

public interface Machine {
  public Result    startHint(Hint h);
  public Machine   endHint(Hint h, Noun product);
  public Result    escape(Noun wish);
  public Dashboard dashboard();
}
