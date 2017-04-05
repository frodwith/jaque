package net.frodwith.jaque.data;

import net.frodwith.jaque.truffle.TypesGen;

/* Because we must use Object fields for the head and the tail to accomodate the atom
 * types that we are using, it is unfortunately possible to construct a cell of any
 * arbitrary Java objects (including, sometimes frustratingly, cells of ints instead of
 * longs etc.). In particular, suffix literal atoms with L (1L, etc) religiously to avoid
 * this. No real checking is done at runtime.
 */

public class Cell {
  public final Object head;
  public final Object tail;

  private int hash;
  
  public Cell(Object head, Object tail) {
    this.head = head;
    this.tail = tail;
  }
  
  private static int mug_both(int lef, int rit) {
    int bot, out;
    while ( true ) {
      bot = Noun.mug_fnv(lef ^ Noun.mug_fnv(rit));
      out = Noun.mug_out(bot);
      if ( 0 != out ) {
        return out;
      }
      else {
        ++rit;
      }
    }
  }
  
  public static boolean equals(Cell a, Cell b) {
    if (a == b) {
      return true;
    }
    if (a.hash != 0 && b.hash != 0 && a.hash != b.hash) {
      return false;
    }
    else {
      return Noun.equals(a.head, b.head) && Noun.equals(a.tail, b.tail);
    }
  }
  
  public static int mug(Cell c) {
    if ( c.hash == 0 ) {
      c.hash = mug_both(Noun.mug(c.head), Noun.mug(c.tail));
    }
    return c.hash;
  }

  public boolean equals(Object o) {
    return TypesGen.isCell(o) && equals(this, TypesGen.asCell(o));
  }
  
  public int hashCode() {
    return mug(this);
  }
  
  public String toString() {
    return Noun.toString(this);
  }
}