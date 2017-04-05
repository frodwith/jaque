package net.frodwith.jaque.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import net.frodwith.jaque.truffle.TypesGen;

/* This is not the base class for nock objects, because nock objects are Objects.
 * This is where we put static library methods that operate on all nouns.
 * 
 */
public class Noun {
  
  public static boolean equals(Object a, Object b) {
    if ( TypesGen.isLong(a) && TypesGen.isLong(b) ) {
      return TypesGen.asLong(a) == TypesGen.asLong(b);
    }
    else if ( TypesGen.isIntArray(a) && TypesGen.isIntArray(b) ) {
      return Arrays.equals(TypesGen.asIntArray(a), TypesGen.asIntArray(b));
    }
    else if ( TypesGen.isCell(a) && TypesGen.isCell(b) ) {
      return Cell.equals(TypesGen.asCell(a), TypesGen.asCell(b));
    }
    else {
      return false;
    }
  }
  
  public static boolean isAtom(Object noun) {
    return TypesGen.isLong(noun) || TypesGen.isIntArray(noun);
  }

  public static boolean isCell(Object noun) {
    return TypesGen.isCell(noun);
  }

  public static boolean isNoun(Object obj) {
    return isCell(obj) || isAtom(obj);
  }
  
  public static Object key(Object noun) {
    if ( isCell(noun) ) {
      return noun;
    }
    return new Atom(noun);
  }

  public static int mug(Object noun) {
    if ( noun instanceof Cell) {
      return Cell.mug((Cell) noun);
    }
    else {
      return Atom.mug(noun);
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
      Cell end = new Cell(head, tail);

      while ( len-- > 0 ) {
        end = new Cell(readRec(a.remove(len)), end);
      }

      return end;
    }
    else {
      throw new IllegalArgumentException();
    }
  }
  
  public static String toString(Object noun) {
    StringBuilder b = new StringBuilder();
    write(b, noun, false);
    return b.toString();
  }

  private static void write(StringBuilder b, Object noun, boolean tail) {
    if ( isCell(noun) ) {
      Cell c = TypesGen.asCell(noun);
      if ( !tail ) {
        b.append("[");
      }
      write(b, c.head, false);
      b.append(" ");
      write(b, c.tail, true);
      if ( !tail ) {
        b.append("]");
      }
    }
    else {
      Atom.write(b, TypesGen.asImplicitIntArray(noun), 16);
    }
  }
  
}
