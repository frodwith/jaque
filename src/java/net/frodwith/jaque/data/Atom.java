package net.frodwith.jaque.data;

import com.sangupta.murmur.Murmur3;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

import gnu.math.MPN;
import net.frodwith.jaque.Bail;
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
 * Many of these methods were adapted from urbit's vere code. The ones
 * referencing MPN were also developed with reference to Kawa Scheme's
 * IntNum class.
 */

public class Atom {
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
  private static final int[] MINIMUM_INDIRECT = new int[] {0, 0, 1};

  public static final boolean BIG_ENDIAN = true;
  public static final boolean LITTLE_ENDIAN = false;

  public static final long YES = 0L;
  public static final long NO = 1L;

  /* Don't try to make these an enum somewhere. Or do, see if I care. But I warned you. */
  public static final Object FAST = mote("fast"),
                             MEMO = mote("memo"),
                             SPOT = mote("spot"),
                             MEAN = mote("mean"),
                             HUNK = mote("hunk"),
                             LOSE = mote("lose"),
                             SLOG = mote("slog"),
                             LEAF = mote("leaf"),
                             ROSE = mote("rose"),
                             PALM = mote("palm");

  public static long mote(String s) {
    return expectUnsignedInt(stringToCord(s));
  }

  private static final NumberFormat dotted = NumberFormat.getNumberInstance(Locale.GERMAN);
  public final Object value;
  
 /*
 * Only make an instance if you need .equals and .hashCode() for a map, etc.
 */ 
  public Atom(Object atom) {
    assert Noun.isAtom(atom);
    this.value = atom;
  }
  
  public static int[] add(int[] a, int[] b) {
    Square s   = new Square(a, b);
    int[] dst  = new int[s.len+1];
    dst[s.len] = MPN.add_n(dst, s.x, s.y, s.len);
    return (int[]) malt(dst);
  }
  
  public static long add(long a, long b) throws ArithmeticException {
    long c = a + b;
    if ( Long.compareUnsigned(c, a) < 0 ||
         Long.compareUnsigned(c, b) < 0 ) {
      throw new ArithmeticException();
    }
    return c;
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
  
  public static Object bex(long a) {
    if (a < 64) {
      return 1L << a;
    }
    else {
      int whole = (int) (a >> 5),
          parts = (int) a & 31;

      int[] words = new int[whole+1];
      words[whole] = 1 << parts;
      return words;
    }
  }
  
  public static Object can(byte a, Iterable<Object> b) {
    int tot = 0;

    try {
      for ( Object i : b ) {
        Cell c = TypesGen.expectCell(i);
        long pil = TypesGen.expectLong(c.head);
        int pi = (int) pil;
        
        if (pi != pil) {
          throw new Bail();
        }

        Object qi = c.tail;
        if ( !Noun.isAtom(qi) ) {
          throw new Bail();
        }
        tot = Math.addExact(tot, pi);
      }
    }
    catch (ArithmeticException e) {
      throw new Bail();
    }
    catch (UnexpectedResultException e) {
      throw new Bail();
    }

    if ( 0 == tot ) {
      return 0L;
    }

    int[] sal = slaq(a, tot);
    int pos = 0;
    
    for ( Object i : b ) {
      Cell c = TypesGen.asCell(i);
      int pi = (int) TypesGen.asLong(c.head);
      chop(a, 0, pi, pos, sal, c.tail);
      pos += pi;
    }

    return malt(sal);
  }
  
  public static int cap(Object atom) {
    int b = met(atom);
    if ( b < 2 ) {
      throw new Bail();
    }
    return getNthBit(atom, b - 2) ? 3 : 2;
  }

  public static Object cat(byte a, Object b, Object c) {
    int lew = met(a, b),
        ler = met(a, c),
        all = lew + ler;
    
    if ( 0 == all ) {
      return 0L;
    }
    else {
      int[] sal = slaq(a, all);

      chop(a, 0, lew, 0, sal, b);
      chop(a, 0, ler, lew, sal, c);

      return malt(sal);
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
  
  public static int compare(int[] a, int[] b) {
    return MPN.cmp(a, a.length, b, b.length);
  } 
  
  public static int compare(long a, long b) {
    return Long.compareUnsigned(a, b);
  }
  
  // -1, 0, 1 for less than, equal, or greater than respectively
  public static int compare(Object a, Object b) {
    if ( TypesGen.isLong(a) && TypesGen.isLong(b) ) {
      return compare(TypesGen.asLong(a), TypesGen.asLong(b));
    } 
    return compare(TypesGen.asImplicitIntArray(a), TypesGen.asImplicitIntArray(b));
  }
  
  public static long con(long a, long b) {
    return a | b;
  }

  public static Object con(Object a, Object b) {
    byte w   = 5;
    int  lna = met(w, a);
    int  lnb = met(w, b);

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

      return malt(sal);
    } 
  }
  
  public static String cordToString(Object atom) {
    try {
      return new String(toByteArray(atom, LITTLE_ENDIAN), "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      return null;
    }
  }
  
  private static Cell cue(Map<Object,Object> m, Object a, Object b) throws UnexpectedResultException {
    Object p, q;

    if ( isZero(cut((byte) 0, b, 1L, a)) ) {
      Object x = increment(b);
      Cell   c = rub(x, a);

      p = increment(c.head);
      q = c.tail;
      m.put(Noun.key(b), q);
    }
    else {
      Object c = add(2L, b),
             l = increment(b);
      
      if ( isZero(cut((byte) 0, l, 1L, a)) ) {
        Cell u, v;
        Object w, x, y;
        
        u = cue(m, a, c);
        x = add(u.head, c);
        v = cue(m, a, x);
        w = new Cell(
            TypesGen.expectCell(u.tail).head,
            TypesGen.expectCell(v.tail).head);
        y = add(u.head, v.head);
        p = add(2L, y);
        q = w;
        m.put(Noun.key(b), q);
      }
      else {
        Cell d = rub(c, a);
        Object x = m.get(Noun.key(d.tail));
        
        if ( null == x ) {
          throw new Bail();
        }

        p = add(2L, d.head);
        q = x;
      }
    }
    return new Cell(p, new Cell(q, 0L));
  }
  
  public static Object cue(Object a) {
    try {
      Cell x = cue(new HashMap<Object,Object>(), a, 0L);
      return TypesGen.expectCell(x.tail).head;
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }

  public static Object cut(byte a, Object b, Object c, Object d) {
    int ci, bi = expectInt(b);
    try {
      ci = expectInt(c);
    } 
    catch (Bail e) {
      ci = 0x7fffffff;
    }
    int len = met(a, d);

    if ( (0 == ci) || (bi >= len) ) {
      return 0L;
    }

    if ( (bi + ci) > len ) {
      ci = len - bi;
    }

    if ( (bi == 0) && (ci == len) ) {
      return d;
    }
    else {
      int[] sal = slaq(a, ci);
      chop(a,  bi, ci, 0, sal, d);
      return malt(sal);
    }
  }
  
  public static Object dec(int[] atom) {
    int[] result;
    if ( atom[0] == 0 ) {
      result = new int[atom.length - 1];
      Arrays.fill(result, 0xFFFFFFFF);
    }
    else {
      result = Arrays.copyOf(atom, atom.length);
      result[0] -= 1;
    }
    return malt(result);
  }

  public static long dec(long atom) {
    if ( atom == 0 ) {
      throw new Bail();
    }
    else {
      return atom - 1;
    }
  }
  
  public static Object dec(Object atom) {
    if ( TypesGen.isLong(atom) ) {
      return dec(TypesGen.asLong(atom));
    }
    else {
      return dec(TypesGen.asIntArray(atom));
    }
  }
  
  public static long dis(long a, long b) {
    return a & b;
  }

  public static Object dis(Object a, Object b) {
    byte w   = 5;
    int  lna = met(w, a);
    int  lnb = met(w, b);

    if ( (0 == lna) && (0 == lnb) ) {
      return 0L;
    }
    else {
      int i, len = Math.max(lna, lnb);
      int[] sal  = new int[len];
      int[] bow  = TypesGen.asImplicitIntArray(b);

      chop(w, 0, lna, 0, sal, a);

      for ( i = 0; i < len; i++ ) {
        sal[i] &= (i >= lnb) ? 0 : bow[i];
      }

      return malt(sal);
    } 
  }
  
  public static Object div(int[] x, int[] y) {
    int cmp = compare(x,y);
    if ( cmp < 0 ) {
      return 0L;
    }
    else if ( 0 == cmp ) {
      return 1L;
    }
    else if ( 1 == y.length ) {
      int[] q = new int[x.length];
      MPN.divmod_1(q, x, x.length, y[0]);
      return malt(q);
    }
    else {
      return divmod(x,y).head;
    }
  }
  
  public static long div(long a, long b) {
    return Long.divideUnsigned(a, b);
  }
  
  public static Object div(Object a, Object b) {
    if ( TypesGen.isLong(a) && TypesGen.isLong(b) ) {
      return div(TypesGen.asLong(a), TypesGen.asLong(b));
    }
    return div(TypesGen.asImplicitIntArray(a), TypesGen.asImplicitIntArray(b));
  }
  
  /* This code is substantially adapted from Kawa's IntNum.java -- see the note at
   * the top of gnu.math.MPN */
  private static Cell divmod(int[] x, int[] y) {
    int xlen = x.length,
        ylen = y.length,
        rlen, qlen;
    int[] xwords = Arrays.copyOf(x, xlen+2),
          ywords = Arrays.copyOf(y, ylen);

    int nshift = MPN.count_leading_zeros(ywords[ylen-1]);
    if (nshift != 0) {
      MPN.lshift(ywords, 0, ywords, ylen, nshift);
      int x_high = MPN.lshift(xwords, 0, xwords, xlen, nshift);
      xwords[xlen++] = x_high;
    }

    if (xlen == ylen) {
      xwords[xlen++] = 0;
    }

    MPN.divide(xwords, xlen, ywords, ylen);
    rlen = ylen;
    MPN.rshift0(ywords, xwords, 0, rlen, nshift);
    qlen = xlen + 1 - ylen;
    xwords = Arrays.copyOfRange(xwords, ylen, ylen+qlen);
    while ( rlen > 1 && 0 == ywords[rlen - 1] ) {
      --rlen; 
    }
    if ( ywords[rlen-1] < 0 ) {
      ywords[rlen++] = 0;
    }
    
    return new Cell(malt(xwords), malt(ywords));
  }

  public static Cell dvr(int[] x, int[] y) {
    int cmp = compare(x,y);
    if ( cmp < 0 ) {
      return new Cell(0L, y);
    }
    else if ( 0 == cmp ) {
      return new Cell(1L, 0L);
    }
    else if ( 1 == y.length ) {
      int[] q = new int[x.length];
      int rem = MPN.divmod_1(q, x, x.length, y[0]);
      return new Cell(malt(q), (long) rem);
    }
    else {
      return divmod(x,y);
    }   
  }

  public static Cell dvr(long a, long b) {
    return new Cell(a / b, a % b);
  }
  
  public static Cell dvr(Object a, Object b) {
    if ( TypesGen.isLong(a) && TypesGen.isLong(b) ) {
      return dvr(TypesGen.asLong(a), TypesGen.asLong(b));
    }
    return dvr(TypesGen.asImplicitIntArray(a), TypesGen.asImplicitIntArray(b));
  }
  
  public static Object end(byte a, Object b, Object c) {
    int bi;

    try {
      bi = expectInt(b);
    }
    catch (Bail e) {
      return c;
    }
    if ( 0 == bi ) {
      return 0L;
    }

    int len = met(a, c);
    if ( bi >= len ) {
      return c;
    }

    int[] sal = slaq(a, bi);
    chop(a, 0, bi, 0, sal, c);
    return malt(sal);
  }
  
  public static boolean equals(int[] a, int[] b) {
    return Arrays.equals(a,  b);
  }
  
  @Override
  public boolean equals(Object b) {
    return (b instanceof Atom)
        && equals(value, ((Atom) b).value);
  }
  
  public static boolean equals(long a, long b) {
    return a == b;
  }
  
  public static boolean equals(Object a, Object b) {
    return ( TypesGen.isLong(a) 
        && TypesGen.isLong(b)
        && equals(TypesGen.asLong(a), TypesGen.asLong(b)) )
        || Arrays.equals(TypesGen.asImplicitIntArray(a), TypesGen.asImplicitIntArray(b));
  }
  
  public static Object expect(Object o) {
    if ( !Noun.isAtom(o) ) {
      throw new Bail();
    }
    return o;
  }
  
  public static byte expectBloq(long atom) {
    if ( atom >= 32 || atom < 0) {
      throw new Bail();
    }
    return (byte) atom;
  }
  
  public static int expectInt(Object a) {
    long al = expectLong(a);
    int  ai = (int) al;
    if ( al != ai ) {
      throw new Bail();
    }
    return ai;
  }
  
  public static int expectUnsignedInt(Object a) {
    long al  = expectLong(a);
    if ( al != (al & 0x00000000FFFFFFFFL) ) {
      throw new Bail();
    }
    return (int) al;
  }

  public static long expectLong(Object a) {
    try {
      return TypesGen.expectLong(a);
    }
    catch (UnexpectedResultException e) {
      throw new Bail();
    }
  }
  
  public static Object fromByteArray(byte[] pill, boolean endian) {
    int len  = pill.length;
    int trim = len % 4;

    if (endian == BIG_ENDIAN) {
      pill = Arrays.copyOf(pill, len);
      reverseBytes(pill);
    }

    if (trim > 0) {
      int    nlen = len + (4-trim);
      byte[] npil = new byte[nlen];
      System.arraycopy(pill, 0, npil, 0, len);
      pill = npil;
      len = nlen;
    }

    int   size  = len / 4;
    int[] words = new int[size];
    int i, b, w;
    for (i = 0, b = 0; i < size; ++i) {
      w =  (pill[b++] << 0)  & 0x000000FF;
      w ^= (pill[b++] << 8)  & 0x0000FF00;
      w ^= (pill[b++] << 16) & 0x00FF0000;
      w ^= (pill[b++] << 24) & 0xFF000000;
      words[i] = w;
    }

    return malt(words);
  }
  
  public static Object fromString(String s) {
    return fromString(s, 10);
  }
  
  public static Object fromString(String s, int radix) {
    char[] car = s.toCharArray();
    int    len = car.length,
           cpw = MPN.chars_per_word(radix),
           i;
    byte[] dig = new byte[len];
    int[]  wor = new int[(len / cpw) + 1];

    for (i = 0; i < len; ++i) {
        dig[i] = (byte) Character.digit(car[i], radix);
    }

    MPN.set_str(wor, dig, len, radix);

    return malt(wor);
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
  
  public static boolean getNthBit(long atom, int n) {
    if ( n >= (Long.SIZE - 1) ) {
      return false;
    }
    else {
      return ((atom & (1L << n)) != 0);
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
  
  public int hashCode() {
    return mug(value);
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

  public static long increment(long atom) throws ArithmeticException {
    return Math.incrementExact(atom);
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
  
  public static boolean isZero(Object atom) {
    return TypesGen.isLong(atom) && 0L == TypesGen.asLong(atom);
  }
  
  public static Object lsh(byte bloq, int count, Object atom) {
    int len = met(bloq, atom),
        big;

    if ( 0 == len ) {
      return 0L;
    }
    try {
      big = Math.addExact(count, len);
    }
    catch (ArithmeticException e) {
      throw new Bail();
    }
    
    int[] sal = slaq(bloq, big);
    chop(bloq, 0, len, count, sal, atom);

    return malt(sal);
  }
  
  public static Object malt(int[] words) {
    int bad = 0;

    for ( int i = words.length - 1; i >= 0; --i) {
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

    if ( 0 == words.length ) {
      return 0L;
    }
    else if ( words != null && words.length > 2 ) {
      return words;
    }
    else if (words.length == 1) {
      return words[0] & 0xffffffffL;
    }
    else {
      return ((words[1] & 0xffffffffL) << 32) | (words[0] & 0xffffffffL);
    }
  }
  
  public static Object mas(Object atom) {
    int b = met(atom);
    if ( b < 2 ) {
      throw new Bail();
    }
    Object c = bex(b - 1),
           d = bex(b - 2),
           e = sub(atom, c);
    return con(e, d);
  }
  
  public static int met(byte bloq, Object atom) {
    int gal, daz;

    if ( TypesGen.isLong(atom) ) {
      long v = (long) atom;
      if ( 0 == v ) {
        return 0;
      }
      else {
        int left = (int) (v >>> 32);
        if ( left == 0 ) {
          gal = 0;
          daz = (int) v;
        }
        else {
          gal = 1;
          daz = left; 
        }
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
  
  public static int met(Object atom) {
    return met((byte)0, atom);
  }
  
  public static long mix(long a, long b) {
    return a ^ b;
  }

  public static Object mix(Object a, Object b) {
    byte w   = 5;
    int  lna = met(w, a);
    int  lnb = met(w, b);

    if ( (0 == lna) && (0 == lnb) ) {
      return 0L;
    }
    else {
      int i, len = Math.max(lna, lnb);
      int[] sal  = new int[len];
      int[] bow  = TypesGen.asImplicitIntArray(b);

      chop(w, 0, lna, 0, sal, a);

      for ( i = 0; i < lnb; i++ ) {
        sal[i] ^= bow[i];
      }

      return malt(sal);
    } 
  }

  public static Object mod(int[] x, int[] y) {
    int cmp = compare(x,y);
    if ( cmp < 0 ) {
      return y;
    }
    else if ( 0 == cmp ) {
      return 0L;
    }
    else if ( 1 == y.length ) {
      int[] q = new int[x.length];
      return (long) MPN.divmod_1(q, x, x.length, y[0]);
    }
    else {
      return divmod(x,y).tail;
    }
  }
  
  public static long mod(long a, long b) {
    return Long.remainderUnsigned(a, b);
  }
  
  public static Object mod(Object a, Object b) {
    if ( TypesGen.isLong(a) && TypesGen.isLong(b) ) {
      return mod(TypesGen.asLong(a), TypesGen.asLong(b));
    }
    return mod(TypesGen.asImplicitIntArray(a), TypesGen.asImplicitIntArray(b));
  }
  
  public static int mug(Object atom) {
    int[] words = TypesGen.asImplicitIntArray(atom);
    return mug_words((int) 2166136261L, words.length, words);
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
  
  public static Object mul(int[] x, int[] y) {
    int xlen = x.length,
        ylen = y.length;
    int[] dest = new int[xlen + ylen];
       
    if ( xlen < ylen ) {
      int zlen = xlen;
      int[] z = x;

      x = y;
      y = z;
      xlen = ylen;
      ylen = zlen;
    }

    MPN.mul(dest, x, xlen, y, ylen);
    return malt(dest);
  }

  public static long mul(long a, long b) throws ArithmeticException {
    return Math.multiplyExact(a, b);
  }

  public static Object mul(Object a, Object b) {
    if ( TypesGen.isLong(a) && TypesGen.isLong(b) ) {
      try {
        return mul(TypesGen.asLong(a), TypesGen.asLong(b));
      }
      catch (ArithmeticException e) {
      }
    }
    return mul(TypesGen.asImplicitIntArray(a), TypesGen.asImplicitIntArray(b));
  }
  
  public static long muk(int seed, int length, Object atom) {
    assert Atom.met((byte) 5, seed) <= 1;
    assert Atom.met(length) <= 31;
    return Murmur3.hash_x86_32(toByteArray(atom, LITTLE_ENDIAN), length, seed);
  }

  public static Object peg(Object axis, Object to) {
    if ( equals(to, 1L) ) {
      return axis;
    }
    else {
      int c = met(to),
          d = c - 1;

      Object e = lsh((byte)0, d, 1L),
             f = sub(to, e),
             g = lsh((byte) 0, d, axis);
      
      return add(f, g);
    }
  }
  
  public static Object rap(byte a, Iterable<Object> b) {
    int tot = 0;
    try {
      for ( Object i : b ) {
        tot = Math.addExact(tot, met(a, Atom.expect(i)));
      }
    }
    catch ( ArithmeticException e ) {
      throw new Bail();
    }
      
    if ( 0 == tot ) {
      return 0L;
    }
    
    int[] sal = slaq(a, tot);
    int pos = 0;
    
    for ( Object i : b ) {
      int len = met(a, i);
      chop(a, 0, len, pos, sal, i);
      pos += len;
    }
    
    return malt(sal);
  }
  
  public static Object rep(byte a, Iterable<Object> b) {
    int tot = 0;
    try {
      for ( Object i : b ) {
        Atom.expect(i);
        tot = Math.incrementExact(tot);
      }
    }
    catch ( ArithmeticException e ) {
      throw new Bail();
    }
    
    int[] sal = slaq(a, tot);
    int pos = 0;
    
    for ( Object i : b ) {
      chop(a, 0, 1, pos++, sal, i);
    }
    return malt(sal);
  }
  
  public static Object rip(byte a, Object b) {
    int[] words = TypesGen.asImplicitIntArray(b);
    Object pir = 0L;
    if ( a < 5 ) {
      int met = met(a, b),
          mek = ((1 << (1 << a)) - 1);

      for ( int i = 0; i < met; ++i ) {
        int pat = met - (i + 1),
            bit = pat << a,
            wor = bit >>> 5,
            sif = bit & 31,
            src = words[wor],
            rip = (src >> sif) & mek;

        pir = new Cell((long) rip, pir);
      }
    }
    else {
      byte sang = (byte) (a - 5);
      int met = met(a, b),
          len = met((byte) 5, b),
          san = 1 << sang,
          dif = (met << sang) - len,
          tub = (0 == dif) ? san : san - dif;

      for ( int i = 0; i < met; ++i ) {
        int pat = met - (i + 1),
            wut = pat << sang,
            sap = ((0 == i) ? tub : san);
        int[] sal = new int[sap];
        
        for ( int j = 0; j < sap; ++j ) {
          sal[j] = words[wut + j];
        }
        
        pir = new Cell(malt(sal), pir);
      }
    }
    return pir;
  }
  
  public static Cell rub(Object a, Object b) {
    Object c, d, e, w, x, y, z, p, q, m;

    m = add(a, (long) met(b));
    x = a;

    while ( isZero(cut((byte)0, x, 1L, b)) ) {
      y = increment(x);
      
      //  Sanity check: crash if decoding more bits than available
      if ( compare(x, m) > 0 ) {
        throw new Bail();
      }

      x = y;
    }

    if ( equals(x, a) ) {
      return new Cell(1L, 0L);
    }
    c = sub(x, a);
    d = increment(x);

    x = dec(c);
    y = bex(expectLong(x));
    z = cut((byte)0, d, x, b);

    e = add(y, z);
    w = add(c, c);
    y = add(w, e);
    z = add(d, x);

    p = add(w, e);
    q = cut((byte)0, z, e, b);
    
    return new Cell(p, q);
  }
  
  private static void reverseBytes(byte[] a) {
    int i, j;
    byte b;
    for (i = 0, j = a.length - 1; j > i; ++i, --j) {
      b = a[i];
      a[i] = a[j];
      a[j] = b;
    }
  }
  
  public static Object rsh(byte bloq, int count, Object atom) {
    int len = met(bloq, atom);

    if ( count >= len ) {
      return 0L;
    }
    else {
      int[] sal = slaq(bloq, len - count);

      chop(bloq, count, len - count, 0, sal, atom);

      return malt(sal);
    }
  }

  public static int[] slaq(byte bloq, int len) {
    int big = ((len << bloq) + 31) >>> 5;
    return new int[big];
  }

  public static Object stringToCord(String s) {
    try {
      return fromByteArray(s.getBytes("UTF-8"), LITTLE_ENDIAN);
    }
    catch (UnsupportedEncodingException e) {
      return null;
    }
  }
  
  public static int[] sub(int[] a, int[] b) {
    Square s = new Square(a, b);
    int[] dst = new int[s.len];
    int bor = MPN.sub_n(dst, s.x, s.y, s.len);
    if ( bor != 0 ) {
      throw new Bail();
    }
    return dst;
  }
  
  public static long sub(long a, long b) {
    if ( -1 == compare(a, b) ) {
      throw new Bail();
    }
    else {
      return a - b;
    }
  }

  public static Object sub(Object a, Object b) {
    if ( TypesGen.isLong(a) && TypesGen.isLong(b) ) {
      return sub(TypesGen.asLong(a), TypesGen.asLong(b));
    }
    else {
      int[] aa = TypesGen.asImplicitIntArray(a);
      int[] ba = TypesGen.asImplicitIntArray(b);
      return malt(sub(aa, ba));
    }
  }
  
  public static byte[] toByteArray(Object atom, boolean endian) {
    if (isZero(atom)) {
      return new byte[1];
    }
    int[]  wor = TypesGen.asImplicitIntArray(atom);
    int    bel = met((byte)3, atom);
    byte[] buf = new byte[bel];
    int    w, i, b;
    for (i = 0, b = 0;;) {
      w = wor[i++];

      buf[b++] = (byte) ((w & 0x000000FF) >>> 0);
      if (b >= bel) break;

      buf[b++] = (byte) ((w & 0x0000FF00) >>> 8);
      if (b >= bel) break;

      buf[b++] = (byte) ((w & 0x00FF0000) >>> 16);
      if (b >= bel) break;

      buf[b++] = (byte) ((w & 0xFF000000) >>> 24);
      if (b >= bel) break;
    }
    if (endian == BIG_ENDIAN) {
      reverseBytes(buf);
    }
    return buf;
  }
  
  public String toString() {
    return toString(value);
  }
  
  public static String toString(Object atom) {
    StringWriter out = new StringWriter();
    try {
      pretty(out, TypesGen.asImplicitIntArray(atom));
      return out.toString();
    }
    catch ( IOException e ) {
      return null;
    }
  }

  public static String toString(Object atom, int radix) {
    StringWriter out = new StringWriter();
    try {
      raw(out, TypesGen.asImplicitIntArray(atom), radix);
      return out.toString();
    }
    catch ( IOException e ) {
      return null;
    }
  }

  public static void pretty(Writer out, int[] cur) throws IOException {
    if ( 1 == cur.length && Long.compareUnsigned(cur[0], 65536) < 0 ) {
      out.write(dotted.format(cur[0]));
    }
    else {
      String cord = cordToString(cur);
      if ( null != cord ) {
        if ( isTas(cord) ) {
          out.write('%');
          out.write(cordToString(cur));
          return;
        }
        else if ( isTa(cord) ) {
          out.write('\'');
          out.write(cordToString(cur));
          out.write('\'');
          return;
        }
      }
      out.write("0x");
      raw(out, cur, 16);
    }
  }

  private static boolean isTas(String s) {
    for ( char c : s.toCharArray() ) {
      if ( !Character.isLowerCase(c)
          && !Character.isDigit(c)
          && '-' != c) {
        return false;
      }
    }
    return true;
  }

  private static boolean isTa(String s) {
    for ( char c : s.toCharArray() ) {
      if ( c < 32 || c > 127 ) {
        return false;
      }
    }
    return true;
  }

  public static void raw(Writer out, int[] cur, int radix) throws IOException {
    Stack<Integer> digits = new Stack<Integer>();

    int len = cur.length,
        size = len;

    cur = Arrays.copyOf(cur, len);

    while ( true ) {
      digits.push(MPN.divmod_1(cur, cur, size, radix));
      if (cur[len-1] == 0) {
        if (--len == 0) {
          break;
        }
      }
    }
    
    while ( !digits.empty() ) {
      out.write(Character.forDigit(digits.pop(), radix));
    }
  }
}
