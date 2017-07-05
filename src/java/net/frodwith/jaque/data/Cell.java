package net.frodwith.jaque.data;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
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
  public int mug;
  
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
  
  @TruffleBoundary
  public static boolean equals(Cell a, Cell b) {
    if (a == b) {
      return true;
    }
    else if ( 0 != a.mug && 0 != b.mug ) {
      return equalsMugged(a, b);
    }
    else {
      return Noun.equals(a.head, b.head) && Noun.equals(a.tail, b.tail);
    }
  }

  // fast-path: if i am mugged, my head and tail are also mugged
  @TruffleBoundary
  public static boolean equalsMugged(Cell a, Cell b) {
    if ( a == b ) {
      return true;
    }
    else if ( a.mug != b.mug ) {
      return false;
    }
    else {
      return Noun.equalsMugged(a.head, b.head) && Noun.equalsMugged(a.tail, b.tail);
    }
  }

  @TruffleBoundary
  public void calculateMug() {
    if ( 0 == mug ) {
      mug = mug_both(Noun.mug(head), Noun.mug(tail));
    }
  }
  
  public static int getMug(Cell c) {
    return c.hashCode();
  }

  public boolean equals(Object o) {
    return TypesGen.isCell(o) && equals(this, TypesGen.asCell(o));
  }
  
  public int hashCode() {
    calculateMug();
    return mug;
  }
  
  public static Cell expect(Object noun) {
    try {
      return TypesGen.expectCell(noun);
    }
    catch ( UnexpectedResultException e ){
      throw new Bail();
    }
  }
}