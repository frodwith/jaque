package jaque.interpreter;

import jaque.noun.*;

public final class Hint {
  public final Atom kind;
  public final Noun clue;
  public final Noun subject;
  public final Cell formula;

  public Hint(Atom k, Noun c, Noun s, Cell f) {
    this.kind    = k;
    this.clue    = c;
    this.subject = s;
    this.formula = f;
  }
}
