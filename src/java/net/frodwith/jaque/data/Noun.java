package net.frodwith.jaque.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.object.DynamicObject;

import gnu.math.MPN;
import net.frodwith.jaque.Bail;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TypesGen;

/* This is not the base class for nock objects, because nock objects are Objects.
 * This is where we put static library methods that operate on all nouns.
 * 
 */
public class Noun {
  
  private static void writeAtom(StringBuilder b, int[] cur, int radix) {
    int len   = cur.length,
        size  = len,
        i     = b.length(),
        j     = i - 1;

    for(;;) {
      int dig = MPN.divmod_1(cur, cur, size, radix);
      b.append(Character.forDigit(dig, radix));
      ++j;
      if (cur[len-1] == 0) {
        if (--len == 0) {
          break;
        }
      }
    }

    for (; i < j; ++i, --j) {
      char t = b.charAt(j);
      b.setCharAt(j, b.charAt(i));
      b.setCharAt(i, t);
    }
  }
  
  private static void write(StringBuilder b, Object noun) {
    if ( isCell(noun) ) {
      DynamicObject c = asCell(noun);
      b.append("[");
      write(b, Cell.head(c));
      b.append(" ");
      write(b, Cell.tail(c));
      b.append("]");
    }
    else {
      writeAtom(b, TypesGen.asImplicitIntArray(noun), 10);
    }
  }
  
  public static String toString(Object noun) {
    StringBuilder b = new StringBuilder();
    write(b, noun);
    return b.toString();
  }

  public static boolean isAtom(Object noun) {
    return TypesGen.isLong(noun) || TypesGen.isIntArray(noun);
  }

  public static boolean isCell(Object noun) {
    return (noun instanceof TruffleObject) 
        && Context.isJaqueObject((TruffleObject) noun);
  }

  public static boolean isNoun(Object obj) {
    return isCell(obj) || isAtom(obj);
  }
  
  public static DynamicObject asCell(Object noun) {
    assert isCell(noun);
    return (DynamicObject) noun;
  }
  
  public static boolean equals(Object a, Object b) {
    if ( TypesGen.isLong(a) && TypesGen.isLong(b) ) {
      return TypesGen.asLong(a) == TypesGen.asLong(b);
    }
    else if ( TypesGen.isIntArray(a) && TypesGen.isIntArray(b) ) {
      return Arrays.equals(TypesGen.asIntArray(a), TypesGen.asIntArray(b));
    }
    else if ( isCell(a) && isCell(b) ) {
      return Cell.equals(asCell(a), asCell(b));
    }
    else {
      return false;
    }
  }
  
  public static int mug(Object noun) {
    if ( isAtom(noun) ) {
      return Atom.mug(noun);
    }
    else {
      return Cell.mug(asCell(noun));
    }
  }

  /* used by both atom and cell mug methods, so package scope */
  static int mug_fnv(int has) {
    return (has * ((int)16777619));
  }
  
  static int mug_out(int has) {
    return (has >>> 31) ^ (has & 0x7fffffff);
  }
  
  public static Object parse(String src) {
    StringBuilder b = null;
    int i, len = src.length();
    ArrayList<Object> result = new ArrayList<Object>();
    Stack<ArrayList<Object>> s = new Stack<ArrayList<Object>>();
    s.push(result);

    for ( i = 0; i < len; ++i ) {
      char c = src.charAt(i);
      if ( Character.isDigit(c) ) {
        if ( null == b ) {
          b = new StringBuilder();
        }
        b.append(c);
      }
      else if ( c == '.' ) {
        if ( null == b ) {
          throw new IllegalArgumentException(". outside atom at position " + i);
        }
      }
      else {

        if ( null != b ) {
          s.peek().add(b.toString());
          b = null;
        }

        if ( c == '[' ) {
          s.push(new ArrayList<Object>());
        }
        else if ( c == ']' ) {
          if ( s.isEmpty() ) {
            throw new IllegalArgumentException("unbalanced ] at position " + i);
          }
          ArrayList<Object> fin = s.pop();
          if ( fin.size() < 2 ) {
            throw new IllegalArgumentException("cell with less than 2 elements at position" + i);
          }
          if ( s.isEmpty() ) {
            throw new IllegalArgumentException("unbalanced ] at position " + i);
          }
          s.peek().add(fin);
        }
        else if ( Character.isSpaceChar(c) ) {
          continue;
        }
        else {
          throw new IllegalArgumentException("unrecognized character " + c + " at position " + i);
        }
      }
    }

    if ( null != b ) {
      s.peek().add(b.toString());
    }

    if ( s.isEmpty() ) {
      throw new IllegalArgumentException("too many ]");
    }
    else {
      ArrayList<Object> tree = s.pop();
      if ( !s.isEmpty() ) {
        throw new IllegalArgumentException("missing ]");
      }
      if ( 1 != tree.size() ) {
        throw new IllegalArgumentException("multiple nouns not surrounded by brackets");
      }
      return readRec(tree.get(0));
    }
  }

  private static Object readRec(Object o) {
    if (o instanceof String) {
      return Atom.fromString((String) o);
    }
    else if (o instanceof ArrayList<?>) {
      ArrayList<Object> a = (ArrayList<Object>) o;
      int len = a.size();
      Object tail = readRec(a.remove(--len)),
             head = readRec(a.remove(--len));
      DynamicObject end = Context.cons(head, tail);

      while ( len-- > 0 ) {
        end = Context.cons(readRec(a.remove(len)), end);
      }

      return end;
    }
    else {
      throw new IllegalArgumentException();
    }
  }
  
}
