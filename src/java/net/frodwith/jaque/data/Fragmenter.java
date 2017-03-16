package net.frodwith.jaque.data;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.truffle.TypesGen;

public class Fragmenter {
  private final boolean[] path;
  public final Object axis;

  public Fragmenter(Object axis) {
    this.axis = axis;
    int i, j, bits = Atom.measure(axis);
    if ( Atom.isZero(axis) ) {
      this.path = null;
    }
    else {
      this.path = new boolean[bits-1];
      
      for ( i = 0, j = (bits - 2); j >= 0; ++i, --j ) {
        path[i] = Atom.getNthBit(axis, j);
      }
    }
  }
  
  public boolean isZero() {
    return (null == path);
  }
  
  public boolean isLeft() {
    return path != null && !path[0];
  }
  
  public boolean isRight() {
    return path != null && path[0];
  }
  
  public Object fragment(Object noun) {
    try {
      if ( isZero() ) {
        throw new Bail();
      }
      for ( boolean right : path ) {
        Cell c = TypesGen.asCell(noun);
        noun = right ? c.tail : c.head;
      }
      return noun;
    }
    catch (ClassCastException e) {
      throw new Bail();
    }

  }
}
