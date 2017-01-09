package jaque.noun;

import clojure.lang.Seqable;
import java.util.*;

public abstract class Noun implements Seqable {
  public abstract void write(StringBuilder b);
  public abstract boolean isZero();

  public final String toString() {
    StringBuilder b = new StringBuilder();
    write(b);
    return b.toString();
  }

  public static int _mug_fnv(int has) {
    return (has * ((int)16777619));
  }

  public static int _mug_out(int has) {
    return (has >>> 31) ^ (has & 0x7fffffff);
  }

  public static int _mug_both(int lef, int rit) {
    int bot = _mug_fnv(lef ^ _mug_fnv(rit));
    int out = _mug_out(bot);

    if (0 != out) {
      return out;
    }
    else {
      return _mug_both(lef, rit + 1);
    }
  }


  public static int _mug_words(int off, int nwd, int[] wod) {
    int has = _mug_words_in(off, nwd, wod),
        out = _mug_out(has);

    if (0 != out) {
      return out;
    }
    else {
      return _mug_words(off+1, nwd, wod);
    }

  }

  public static int _mug_words_in(int off, int nwd, int[] wod) {
    if (0 == nwd) {
      return off;
    }
    int i, x;
    for (i = 0; i < (nwd - 1); ++i) {
      x = wod[i];

      off = _mug_fnv(off ^ ((x >>> 0)  & 0xff));
      off = _mug_fnv(off ^ ((x >>> 8)  & 0xff));
      off = _mug_fnv(off ^ ((x >>> 16) & 0xff));
      off = _mug_fnv(off ^ ((x >>> 24) & 0xff));
    }
    x = wod[nwd - 1];
    if (x != 0) {
      off = _mug_fnv(off ^ (x & 0xff));
      x >>>= 8;
      if (x != 0) {
        off = _mug_fnv(off ^ (x & 0xff));
        x >>>= 8;
        if (x != 0) {
          off = _mug_fnv(off ^ (x & 0xff));
          x >>>= 8;
          if (x != 0) {
            off = _mug_fnv(off ^ (x & 0xff));
          }
        }
      }
    }
    return off;
  }

  public static Noun read(String src) throws IllegalArgumentException {
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
          ArrayList fin = s.pop();
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
      ArrayList tree = s.pop();
      if ( !s.isEmpty() ) {
        throw new IllegalArgumentException("missing ]");
      }
      if ( 1 != tree.size() ) {
        throw new IllegalArgumentException("multiple nouns not surrounded by brackets");
      }
      return readRec(tree.get(0));
    }
  }

  private static Noun readRec(Object o) throws IllegalArgumentException {
    if (o instanceof String) {
      return Atom.fromString((String) o);
    }
    else if (o instanceof ArrayList) {
      ArrayList a = (ArrayList) o;
      int len = a.size();
      Noun cdr = readRec(a.remove(--len)),
           car = readRec(a.remove(--len));
      Cell end = new Cell(car, cdr);

      while ( len-- > 0 ) {
        end = new Cell(readRec(a.remove(len)), end);
      }

      return end;
    }
    else {
      throw new IllegalArgumentException();
    }
  }
}
