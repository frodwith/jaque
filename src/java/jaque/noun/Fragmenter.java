package jaque.noun;

import jaque.interpreter.Bail;

public class Fragmenter {
  private final boolean[] path;
  
  public Fragmenter(Atom axis) {
    this.path = axis.isZero() ? null : axis.fragments();
  }
  
  public Object fragment(Object subject) {
    if ( null == path ) {
      throw new Bail();
    }
    try {
      for ( boolean tail : path ) {
        Cell c = (Cell) subject;
        subject = tail ? c.getTail() : c.getHead();
      }
      return subject;
    }
    catch (ClassCastException e) {
      throw new Bail();
    }
  }
}
