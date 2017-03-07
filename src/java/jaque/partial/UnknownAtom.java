package jaque.partial;

import jaque.interpreter.Bail;
import jaque.noun.Atom;
import jaque.noun.Cell;

public class UnknownAtom extends PartialSubject {
  public final UnknownAtom INSTANCE = new UnknownAtom();

  private UnknownAtom() {
  }
  
  public boolean fine(Object test) {
    return !(test instanceof Cell);
  }
  
  public Atom[] getSamples() {
    return null;
  }
  
  public Object fragment(Atom axis, Object[] arguments) {
    throw new Bail();
  }
}

