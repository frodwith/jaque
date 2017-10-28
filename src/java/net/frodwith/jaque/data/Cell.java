package net.frodwith.jaque.data;

import java.io.Serializable;
import java.util.Stack;

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

public class Cell implements Serializable {
  // head and tail are not final because we set them during unifying equals
  public Object head, tail;
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
      if ( Noun.equals(a.head, b.head) ) {
        if ( 0 == a.mug && 0 != b.mug ) {
          // if we throw away mugs, it violates our fast-path assumption
          // also it's just a bad idea
          Cell tmp = b;
          b = a;
          a = tmp;
        }
        b.head = a.head;
        if ( Noun.equals(a.tail, b.tail) ) {
          b.tail = a.tail;
          b.mug = a.mug;
          return true;
        }
      }
      return false;
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
      if ( Noun.equalsMugged(a.head, b.head) ) {
        b.head = a.head;
        if ( Noun.equalsMugged(a.tail, b.tail) ) {
          b.tail = a.tail;
          return true;
        }
      }
      return false;
    }
  }
  
  private static Cell shouldMug(Object o) {
    if ( TypesGen.isCell(o) ) {
      Cell c = TypesGen.asCell(o);
      if ( 0 == c.mug ) {
        return c;
      }
    }
    return null;
  }

  @TruffleBoundary
  public void calculateMug() {
    if ( 0 == mug ) {
      // recursion here can cause stack overflows for large nouns,
      // so we do an explicit iterative post-order traversal
      Stack<Cell> s = new Stack<Cell>();
      Object last = null;
      Cell node = this;
      while ( null != node || !s.empty() ) {
        if ( null != node ) {
          s.push(node);
          node = shouldMug(node.head);
        }
        else {
          Cell peek = s.peek(),
               rite = shouldMug(peek.tail);

          if ( null != rite && last != rite ) {
            node = rite;
          }
          else {
            peek.mug = mug_both(Noun.mug(peek.head), Noun.mug(peek.tail));
            last = s.pop();
          }
        }
      }
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
  
  public static Cell expect(Object noun) throws UnexpectedResultException {
    return TypesGen.expectCell(noun);
  }
  
  public static Cell orBail(Object noun) {
    try {
      return TypesGen.expectCell(noun);
    }
    catch ( UnexpectedResultException e ){
      throw new Bail();
    }
  }
}