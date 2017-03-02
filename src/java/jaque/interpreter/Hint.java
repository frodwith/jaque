package jaque.interpreter;

import jaque.noun.*;

public final class Hint {
  public final Atom kind;
  public final Object clue;
  public final Object subject;
  public final Cell formula;

  public Hint(Atom k, Object c, Object s, Cell f) {
    this.kind    = k;
    this.clue    = c;
    this.subject = s;
    this.formula = f;
  }
}
