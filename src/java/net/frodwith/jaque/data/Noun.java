package net.frodwith.jaque.data;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.truffle.TypesGen;

/* This is not the base class for nock objects, because nock objects are Objects.
 * This is where we put static library methods that operate on all nouns.
 * 
 */
public class Noun {
  // Arguments may be mutated during unification
  public static boolean equals(Object a, Object b) {
    if ( a == b ) {
      return true;
    }
    else if ( TypesGen.isCell(a) ) {
      if ( TypesGen.isCell(b) ) {
        return Cell.equals(TypesGen.asCell(a), TypesGen.asCell(b));
      }
      else {
        return false;
      }
    }
    else if ( TypesGen.isCell(b) ) {
      return false;
    }
    else {
      return Atom.equals(a, b);
    }
  }

  public static boolean equalsMugged(Object a, Object b) {
    if ( a == b ) {
      return true;
    }
    else if ( TypesGen.isCell(a) ) {
      if ( TypesGen.isCell(b) ) {
        return Cell.equalsMugged(TypesGen.asCell(a), TypesGen.asCell(b));
      }
      else {
        return false;
      }
    }
    else if ( TypesGen.isCell(b) ) {
      return false;
    }
    else {
      return Atom.equals(a, b);
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
    if ( TypesGen.isCell(noun) ) {
      return Cell.getMug(TypesGen.asCell(noun));
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

  @TruffleBoundary
  public static Object parse(String src) {
    StringBuilder b = null;
    int i, len = src.length();
    ArrayList<Object> result = new ArrayList<Object>();
    Deque<ArrayList<Object>> s = new ArrayDeque<ArrayList<Object>>();
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
  
  @SuppressWarnings("unchecked")
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
  
  @TruffleBoundary
  public static String toString(Object noun) {
    StringWriter out = new StringWriter();
    try {
      pretty(out, noun, false);
      return out.toString();
    }
    catch ( IOException e ) {
      return null;
    }
  }

  @TruffleBoundary
  private static void pretty(Writer out, Object noun, boolean tail) throws IOException {
    if ( isCell(noun) ) {
      Cell c = TypesGen.asCell(noun);
      if ( !tail ) {
        out.write('[');
      }
      pretty(out, c.head, false);
      out.write(' ');
      pretty(out, c.tail, true);
      if ( !tail ) {
        out.write(']');
      }
    }
    else {
      Atom.pretty(out, TypesGen.asImplicitIntArray(noun));
    }
  }
  
  @TruffleBoundary
  public static void print(Object noun) {
    print(noun, new OutputStreamWriter(System.out));
  }

  @TruffleBoundary
  public static void println(Object noun) {
    println(noun, new OutputStreamWriter(System.out));
  }
  
  @TruffleBoundary
  public static void print(Object noun, OutputStreamWriter out) {
    try {
      pretty(out, noun, false);
      out.flush();
    }
    catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  @TruffleBoundary
  public static void println(Object noun, OutputStreamWriter out) {
    print(noun, out);
    try {
      out.write('\n');
      out.flush();
    }
    catch ( IOException e ) {
      e.printStackTrace();
    }
  }

}
