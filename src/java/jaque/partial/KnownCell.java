package jaque.partial;

import jaque.noun.Atom;
import jaque.noun.Cell;
import jaque.noun.Fragmenter;

public class KnownCell extends PartialSubject {
  private final Cell known;
  
  public KnownCell(Cell cell) {
    this.known = cell;
  }
  
  public boolean fine(Object test) {
    return known.equals(test);
  }
  
  public Atom[] getSamples() {
    return null;
  }
  
  public Object fragment(Atom axis, Object[] arguments) {
    return new Fragmenter(axis).fragment(known);
  }
}
