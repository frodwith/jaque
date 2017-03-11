package net.frodwith.jaque.data;

/* Because we must use Object fields for the head and the tail to accomodate the atom
 * types that we are using, it is unfortunately possible to construct a cell of any
 * arbitrary Java objects (including, sometimes frustratingly, cells of ints instead of
 * longs etc.). In particular, suffix literal atoms with L (1L, etc) religiously to avoid
 * this. No real checking is done at runtime.
 */

public class Cell {
  public final Object head;
  public final Object tail;

  private boolean hashed;
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
    if (a.hashed && b.hashed && a.hash != b.hash) {
      return false;
    }
    else {
      return Noun.equals(a.head, b.head) && Noun.equals(a.tail, b.tail);
    }
  }
  
  public static int mug(Cell c) {
    if ( !c.hashed ) {
      c.hash = mug_both(Noun.mug(c.head), Noun.mug(c.tail));
      c.hashed = true;
    }
    return c.hash;
  }

  public boolean equals(Object o) {
    return (o instanceof Cell) && equals(this, (Cell) o);
  }
  
  public int hashCode() {
    return mug(this);
  }
}