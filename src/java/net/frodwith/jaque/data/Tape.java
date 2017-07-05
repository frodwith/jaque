package net.frodwith.jaque.data;

public class Tape {

  public static Object runt(Object count, Object atom, Object list) {
    while ( !Atom.isZero(count) ) {
      list = new Cell(atom, list);
      count = Atom.dec(count);
    }
    return list;
  }

}
