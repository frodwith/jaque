package jaque.partial;

import jaque.noun.Atom;
import jaque.noun.Cell;
import jaque.noun.Fragmenter;

public class UnknownCell extends PartialSubject {
  public final UnknownCell INSTANCE = new UnknownCell();
  public final Atom[] both = { Atom.TWO, Atom.THREE };
  
  private UnknownCell() {
  }
  
  public boolean fine(Object test) {
    return test instanceof Cell;
  }
  
  public Atom[] getSamples() {
    return both;
  }
  
  public Object fragment(Atom axis, Object[] arguments) {
    return new Fragmenter(axis.mas()).fragment(2 == axis.cap() ? arguments[0] : arguments[1]);
  }
}

