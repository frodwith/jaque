package net.frodwith.jaque.data;

import java.util.Arrays;

import gnu.math.MPN;
import net.frodwith.jaque.Bail;
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
      Cell c = TypesGen.asCell(noun);
      b.append("[");
      write(b, c.head);
      b.append(" ");
      write(b, c.tail);
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
    return TypesGen.isCell(noun);
  }

  public static boolean isNoun(Object obj) {
    return isCell(obj) || isAtom(obj);
  }

  
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
  
}
