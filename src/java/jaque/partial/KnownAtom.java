package jaque.partial;

import jaque.interpreter.Bail;
import jaque.noun.Atom;

public class KnownAtom extends PartialSubject {
  private final Object known;
  
  public KnownAtom(Object atom) {
    this.known = atom;
  }

  public boolean fine(Object test) {
    try {
      return Atom.coerceAtom(test).equals(known);
    }
    catch (Bail e) {
      return false;
    }
  }
  
  public Atom[] getSamples() {
    return null;
  }

  public Object fragment(Atom axis, Object[] arguments) {
    throw new Bail();
  }
}
