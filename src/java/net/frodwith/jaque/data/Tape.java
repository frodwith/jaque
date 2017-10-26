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
      b.append((char) Atom.intOrBail(atom));
    }
    return b.toString();
  }

  public static Object fromString(String t) {
    char[] cs = t.toCharArray();
    Object r = 0L;
    for (int i = cs.length - 1; i >= 0; --i ) {
      r = new Cell((long) cs[i], r);
    }
    return r;
  }

}
