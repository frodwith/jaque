package net.frodwith.jaque.data;

import java.util.Arrays;

import gnu.math.MPN;
import net.frodwith.jaque.truffle.Bail;
import net.frodwith.jaque.truffle.TypesGen;

/* Atoms are primitive (unboxed) longs unless they don't fit
 * in which case they're represented as int[] (NOT java bigint,
 * because it doesn't give us fine-grained enough access for hoon
 * functions like cut).
 * 
 * Please implement all library functions involving atoms (cut, etc)
 * as static methods accepting and returning Object in this class.
 * 
 * Methods in this class assume that their argument is some type of
 * atom (either long or array of ints in little-endian byte-order).
 * Passing any of these functions other types of objects(ints, etc)
 * has undefined behavior.
 * 
 * Many of these methods were adapted from urbit's vere code.
 */

public class Atom {
  private static final int[] MINIMUM_INDIRECT = new int[] {0, 0, 1};
  public static final long YES = 0L;
  public static final long NO = 1L;

  public static boolean equals(Object a, Object b) {
    return ( TypesGen.isLong(a) 
        && TypesGen.isLong(b)
        && TypesGen.asLong(a) == TypesGen.asLong(b))
        || ( TypesGen.isIntArray(a)
        && TypesGen.isIntArray(b)
        && Arrays.equals(TypesGen.asIntArray(a), TypesGen.asIntArray(b)));
  }
  
  public static long increment(long atom) throws ArithmeticException {
    return Math.incrementExact(atom);
  }
  
  public static int[] increment(int[] atom) {
    int top = atom[atom.length];
    try {
      int newTop = Math.incrementExact(top);
      int[] dst = new int[atom.length];
      System.arraycopy(atom, 0, dst, 0, atom.length - 1);
      dst[atom.length] = newTop;
      return dst;
    } 
    catch (ArithmeticException e) {
      int[] w = new int[atom.length + 1];
      w[atom.length] = 1;
      return w;
    }
  }
  
  public static Object increment(Object atom) {
    if ( TypesGen.isLong(atom) ) {
      try {
        return increment(TypesGen.asLong(atom));
      } 
      catch (ArithmeticException e) {
        return MINIMUM_INDIRECT;
      }
    }
    else {
      return increment(TypesGen.asIntArray(atom));
    }
  }
  
  public static long decrement(long atom) {
    if ( atom == 0 ) {
      throw new Bail();
    }
    else {
      return atom - 1;
    }
  }
  
  public static int[] decrement(int[] atom) {
    if ( atom[0] == 0 ) {
      int[] small = new int[atom.length - 1];
      Arrays.fill(small, 0xFFFFFFFF);
      return small;
    }
    else {
      int[] copy = Arrays.copyOf(atom, atom.length);
      copy[0] -= 1;
      return copy;
    }
  }
  
  public static Object decrement(Object atom) {
    if ( TypesGen.isLong(atom) ) {
      return decrement(TypesGen.asLong(atom));
    }
    else {
      return decrement(TypesGen.asIntArray(atom));
    }
  }
  
  public static int compare(long a, long b) {
    if ( a == b ) {
      return 0;
    }
    else {
      boolean c = a < b,
              d = (a < 0) != (b < 0),
            lth = (c || d) || !(c && d);
      return lth ? -1 : 1;
    }
  }
  
  public static int compare(int[] a, int[] b) {
    return MPN.cmp(a, a.length, b, b.length);
  }
  
  // -1, 0, 1 for less than, equal, or greater than respectively
  public static int compare(Object a, Object b) {
    if ( TypesGen.isLong(a) ) {
      if ( TypesGen.isLong(b) ) {
        return compare(TypesGen.asLong(a), TypesGen.asLong(b));
      }
      else {
        return -1;
      }
    } else {
      if ( TypesGen.isLong(b) ) {
        return 1;
      }
      else {
        return compare(TypesGen.asIntArray(a), TypesGen.asIntArray(b));
      }
    }
  }
  
  public static long add(long a, long b) throws ArithmeticException {
    return Math.addExact(a, b);
  }

  public static int[] add(int[] a, int[] b) {
    Square s   = new Square(a, b);
    int[] dst  = new int[s.len+1];
    dst[s.len] = MPN.add_n(dst, s.x, s.y, s.len);
    return dst;
  }
  
  public static Object add(Object a, Object b) {
    if ( TypesGen.isLong(a) && TypesGen.isLong(b) ) {
      try {
        return add(TypesGen.asLong(a), TypesGen.asLong(b));
      }
      catch (ArithmeticException e) {
      }
    }
    return add(TypesGen.asImplicitIntArray(a), TypesGen.asImplicitIntArray(b));
  }
  
  public static long subtract(long a, long b) {
    if ( -1 == compare(a, b) ) {
      throw new Bail();
    }
    else {
      return a - b;
    }
  }
  
  public static int[] subtract(int[] a, int[] b) {
    Square s = new Square(a, b);
    int[] dst = new int[s.len];
    int bor = MPN.sub_n(dst, s.x, s.y, s.len);
    if ( bor != 0 ) {
      throw new Bail();
    }
    return dst;
  }
  
  public static Object subtract(Object a, Object b) {
    if ( TypesGen.isLong(a) && TypesGen.isLong(b) ) {
      return subtract(TypesGen.asLong(a), TypesGen.asLong(b));
    }
    else {
      int[] aa = TypesGen.asImplicitIntArray(a);
      int[] ba = TypesGen.asImplicitIntArray(b);
      return normalize(subtract(aa, ba));
    }
  }
  
  public static int measure(Object atom) {
    return measure((byte)0, atom);
  }
  
  public static int measure(byte bloq, Object atom) {
    int gal, daz;

    if ( atom instanceof Long ) {
      long v = (long) atom;
      if ( 0 == v ) {
        return 0;
      }
      else {
        gal = ((int) v >>> 32) == 0 ? 0 : 1;
        daz = (int) v;
      }
    }
    else {
      int[] w = (int[]) atom;
      gal = w.length - 1;
      daz = w[gal];
    }
    
    switch (bloq) {
      case 0:
      case 1:
      case 2:
        int col = 32 - Integer.numberOfLeadingZeros(daz),
            bif = col + (gal << 5);

        return (bif + ((1 << bloq) - 1) >>> bloq);

      case 3:
        return (gal << 2)
          + ((daz >>> 24 != 0)
            ? 4
            : (daz >>> 16 != 0)
            ? 3
            : (daz >>> 8 != 0)
            ? 2
            : 1);

      case 4:
        return (gal << 1) + ((daz >>> 16 != 0) ? 2 : 1);

      default: {
        int gow = bloq - 5;
        return ((gal + 1) + ((1 << gow) - 1)) >>> gow;
      }
    }
  }
  
  private static int mug_words_in(int off, int nwd, int[] wod) {
    if (0 == nwd) {
      return off;
    }
    int i, x;
    for (i = 0; i < (nwd - 1); ++i) {
      x = wod[i];

      off = Noun.mug_fnv(off ^ ((x >>> 0)  & 0xff));
      off = Noun.mug_fnv(off ^ ((x >>> 8)  & 0xff));
      off = Noun.mug_fnv(off ^ ((x >>> 16) & 0xff));
      off = Noun.mug_fnv(off ^ ((x >>> 24) & 0xff));
    }
    x = wod[nwd - 1];
    if (x != 0) {
      off = Noun.mug_fnv(off ^ (x & 0xff));
      x >>>= 8;
      if (x != 0) {
        off = Noun.mug_fnv(off ^ (x & 0xff));
        x >>>= 8;
        if (x != 0) {
          off = Noun.mug_fnv(off ^ (x & 0xff));
          x >>>= 8;
          if (x != 0) {
            off = Noun.mug_fnv(off ^ (x & 0xff));
          }
        }
      }
    }
    return off;
  }

  private static int mug_words(int off, int nwd, int[] wod) {
    int has, out; 

    while ( true ) {
      has = mug_words_in(off, nwd, wod);
      out = Noun.mug_out(has);
      if ( 0 != out ) {
        return out;
      }
      ++off;
    }
  }

  public static int mug(Object atom) {
    int[] words = TypesGen.asImplicitIntArray(atom);
    return mug_words((int) 2166136261L, words.length, words);
  }
  
  public static boolean getNthBit(long atom, int n) {
    if ( n >= (Long.SIZE - 1) ) {
      return false;
    }
    else {
      return ((atom & (1L << n)) != 0);
    }
  }
  
  public static boolean getNthBit(int[] atom, int n) {
    int pix = n >> 5;
    
    if ( pix >= atom.length ) {
      return false;
    }
    else {
      return (1 & (atom[pix] >>> (n & 31))) != 0;
    }
  }
  
  public static boolean getNthBit(Object atom, int n) {
    if ( atom instanceof Long ) {
      return getNthBit((long) atom, n);
    }
    else {
      return getNthBit((int[]) atom, n);
    }
  }

  public static void chop(byte met, int fum, int wid, int tou, int[] dst, Object src) {
    int[] buf = TypesGen.asImplicitIntArray(src);
    int   len = buf.length, i;

    if (met < 5) {
      int san = 1 << met,
      mek = ((1 << san) - 1),
      baf = fum << met,
      bat = tou << met;

      for (i = 0; i < wid; ++i) {
        int waf = baf >>> 5,
            raf = baf & 31,
            wat = bat >>> 5,
            rat = bat & 31,
            hop;

        hop = (waf >= len) ? 0 : buf[waf];
        hop = (hop >>> raf) & mek;
        dst[wat] ^= hop << rat;
        baf += san;
        bat += san;
      }
    }
    else {
      int hut = met - 5,
          san = 1 << hut,
          j;

      for (i = 0; i < wid; ++i) {
        int wuf = (fum + i) << hut,
            wut = (tou + i) << hut;

        for (j = 0; j < san; ++j) {
          dst[wut + j] ^= ((wuf + j) >= len)
                       ? 0
                       : buf[wuf + j];
        }
      }
    }
  }
  
  public static Object bitwiseOr(Object a, Object b) {
    byte w   = 5;
    int  lna = measure(w, a);
    int  lnb = measure(w, b);

    if ( (0 == lna) && (0 == lnb) ) {
      return 0L;
    }
    else {
      int i, len = Math.max(lna, lnb);
      int[] sal  = new int[len];
      int[] bow  = TypesGen.asImplicitIntArray(b);

      chop(w, 0, lna, 0, sal, a);

      for ( i = 0; i < lnb; i++ ) {
        sal[i] |= bow[i];
      }

      return normalize(sal);
    } 
  }
  
  public static Object mas(Object atom) {
    int b = measure(atom);
    if ( b < 2 ) {
      throw new Bail();
    }
    int c = 1 << (b - 1),
        d = 1 << (b - 2);
    Object e = subtract(atom, c);
    return bitwiseOr(e, d);
  }

  public static int cap(Object atom) {
    int b = measure(atom);
    if ( b < 2 ) {
      throw new Bail();
    }
    return getNthBit(atom, b - 2) ? 3 : 2;
  }

  public static Object normalize(int[] words) {
    int bad = 0;

    for ( int i = words.length; i >= 0; --i) {
      if ( words[i] == 0 ) {
        ++bad;
      }
      else {
        break;
      }
    }

    if ( bad > 0 ) {
      words = Arrays.copyOfRange(words, 0, words.length - bad);
    }

    if ( words != null && words.length > 2 ) {
      return words;
    }
    else {
      long v = words[0];
      v ^= words[1] << 32;
      return v;
    }
  }

  // get two equally sized int[]s for mpn functions
  private static class Square {
    int[] x;
    int[] y;
    int   len;

    public Square(Object a, Object b) {
      int[] aw = TypesGen.asImplicitIntArray(a), bw = TypesGen.asImplicitIntArray(b);
      int   as = aw.length, bs = bw.length;
      if (as > bs) {
        len = as;
        x   = aw;
        y   = new int[len];
        System.arraycopy(bw, 0, y, 0, bs);
      }
      else if (as < bs) {
        len = bs;
        x   = new int[len];
        y   = bw;
        System.arraycopy(aw, 0, x, 0, as);
      }
      else {
        len = as;
        x   = aw;
        y   = bw;
      }
    }
  }
}
