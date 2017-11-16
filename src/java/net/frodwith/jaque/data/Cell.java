package net.frodwith.jaque.data;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

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
  
  private static class EqFrame {
    public Object a;
    public Object b;
    public boolean returning = false;
    
    public EqFrame(Object a, Object b) {
      this.a = a;
      this.b = b;
    }
  }

  public static boolean equals(Cell a, Cell b) {
    Deque<EqFrame> s = new ArrayDeque<EqFrame>();
    s.push(new EqFrame(a, b));
    while ( !s.isEmpty() ) {
      EqFrame frame = s.peek();

      if ( frame.returning ) {
        Cell ca = TypesGen.asCell(frame.a),
             cb = TypesGen.asCell(frame.b);
        cb.head = ca.head;
        cb.tail = ca.tail;
        s.pop();
      }
      else if ( frame.a == frame.b ) {
        s.pop();
      }
      else if ( TypesGen.isCell(frame.a) ) {
        if ( TypesGen.isCell(frame.b) ) {
          Cell ca = TypesGen.asCell(frame.a),
               cb = TypesGen.asCell(frame.b);

          if ( 0 == ca.mug ) {
            if ( 0 != cb.mug ) {
              // swap to avoid losing mugs
              Cell tmp = ca;
              frame.a = ca = cb;
              frame.b = cb = tmp;
            }
          }
          else if ( 0 != cb.mug ) {
            if ( equalsMugged(ca, cb) ) {
              s.pop();
              continue;
            }
            else {
              return false;
            }
          }

          frame.returning = true;
          s.push(new EqFrame(ca.tail, cb.tail));
          s.push(new EqFrame(ca.head, cb.head));
        }
        else {
          return false;
        }
      }
      else if ( TypesGen.isCell(frame.b) ) {
        return false;
      }
      else if ( !Atom.equals(frame.a, frame.b) ) {
        return false;
      }
      else {
        s.pop();
      }
    }
    return true;
  }

  // fast-path: if i am mugged, my head and tail are also mugged
  public static boolean equalsMugged(Cell a, Cell b) {
    Deque<EqFrame> s = new ArrayDeque<EqFrame>();
    s.push(new EqFrame(a, b));
    while ( !s.isEmpty() ) {
      EqFrame frame = s.peek();

      if ( frame.returning ) {
        Cell ca = TypesGen.asCell(frame.a),
             cb = TypesGen.asCell(frame.b);
        cb.head = ca.head;
        cb.tail = ca.tail;
        s.pop();
      }
      else if ( frame.a == frame.b ) {
        s.pop();
      }
      else if ( TypesGen.isCell(frame.a) ) {
        if ( TypesGen.isCell(frame.b) ) {
          Cell ca = TypesGen.asCell(frame.a),
               cb = TypesGen.asCell(frame.b);
          if ( ca.mug != cb.mug ) {
            return false;
          }
          else {
            frame.returning = true;
            s.push(new EqFrame(ca.tail, cb.tail));
            s.push(new EqFrame(ca.head, cb.head));
          }
        }
        else {
          return false;
        }
      }
      else if ( TypesGen.isCell(frame.b) ) {
        return false;
      }
      else if ( !Atom.equals(frame.a, frame.b) ) {
        return false;
      }
      else {
        s.pop();
      }
    }
    return true;
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
  
  private int mugged(Object o) {
    return TypesGen.isCell(o)
        ? TypesGen.asCell(o).mug
        : Atom.mug(o);
  }

  public void calculateMug() {
    if ( 0 == mug ) {
      // recursion here can cause stack overflows for large nouns,
      // so we do an explicit iterative post-order traversal
      Deque<Cell> s = new ArrayDeque<Cell>();
      Object last = null;
      Cell node = this;
      while ( null != node || !s.isEmpty() ) {
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
            peek.mug = mug_both(mugged(peek.head), mugged(peek.tail));
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