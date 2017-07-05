package net.frodwith.jaque.data;

public class Tape {

  public static Object runt(Object count, Object atom, Object list) {
    while ( !Atom.isZero(count) ) {
      list = new Cell(atom, list);
      count = Atom.dec(count);
    }
    return list;
  }

  public static String toString(Object tape) {
    StringBuilder b = new StringBuilder();
    for ( Object atom : new List(tape)) {
      b.append((char) Atom.expectInt(atom));
    }
    return b.toString();
  }

}
