package jaque.noun;

import gnu.math.MPN;
import java.util.Arrays;
import java.util.Collections;
import clojure.lang.Cons;
import clojure.lang.ISeq;

public abstract class Atom extends Noun implements Comparable<Atom> {
    public static final int    MAX_FIXNUM = 255;
    public static final Atom[] fix;
    public static final Atom ZERO, ONE, TWO, THREE, TEN, YES, NO;
    public static final boolean BIG_ENDIAN = true;
    public static final boolean LITTLE_ENDIAN = false;

    static {
        fix = new DirectAtom[MAX_FIXNUM + 1];
        for (int i = 0; i <= MAX_FIXNUM; ++i) {
            fix[i] = new DirectAtom(i);
        }
        ZERO  = fix[0];
        ONE   = fix[1];
        TWO   = fix[2];
        THREE = fix[3];
        TEN   = fix[10];
        YES   = fix[0];
        NO    = fix[1];
    }

    public abstract int[] cloneWords();
    public abstract int compareTo(Atom a);
    public abstract int hashCode();
    public abstract boolean isZero();
    public abstract int lo();
    public abstract int[] words();
    public abstract boolean isCat();

    public boolean isEven() {
        return (lo() & 1) == 0;
    }

    public ISeq seq() {
        return new Cons(this, null);
    }

    public static Atom fromLong(long l) {
        if (l >= 0 && l < MAX_FIXNUM) {
            return fix[(int) l];
        }
        else if (l - Integer.MAX_VALUE <= 0) {
            return new DirectAtom((int) l);
        }
        else {
            return malt(new int[]{(int) l, (int) (l >>> 32)});
        }
    }

    /* Big endian is the default for toByteArray because that's what
     * BigInteger does, so it's good to keep the same API. */
    public byte[] toByteArray() {
        return toByteArray(BIG_ENDIAN);
    }

    public static void reverseBytes(byte[] a) {
        int i, j;
        byte b;
        for (i = 0, j = a.length - 1; j > i; ++i, --j) {
            b = a[i];
            a[i] = a[j];
            a[j] = b;
        }
    }

    public byte[] toByteArray(boolean endian) {
        if (isZero()) {
            return new byte[1];
        }

        int[]  wor = words();
        int    len = wor.length,
               bel = met((byte)3);
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

    public static Atom fromByteArray(byte[] pill, boolean endian) {
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

    public static Atom fromByteArray(byte[] pill) {
        return fromByteArray(pill, BIG_ENDIAN);
    }

    public static Atom fromString(String s, int radix) {
        char[] car = s.toCharArray();
        int    len = car.length,
               cpw = MPN.chars_per_word(radix),
               siz,
               i;
        byte[] dig = new byte[len];
        int[]  wor = new int[(len / cpw) + 1];

        for (i = 0; i < len; ++i) {
            dig[i] = (byte) Character.digit(car[i], radix);
        }

        siz = MPN.set_str(wor, dig, len, radix);

        return malt(wor);
    }

    public static Atom fromString(String s) {
        return fromString(s, 10);
    }

    public static Atom malt(int[] w) {
        int hi = 0;

        for (int i = 0; i < w.length; ++i) {
            if (w[i] != 0) {
               hi = i;
            }
        }

        if (hi == 0) {
            return fromLong(w[0]);
        }

        if (hi+1 < w.length) {
            w = Arrays.copyOfRange(w, 0, hi+1);
        }

        return new IndirectAtom(w);
    }

    public abstract int intValue();
    public abstract long longValue();
    public byte byteValue() { return (byte) intValue(); }
    public short shortValue() { return (short) intValue(); }
    public float floatValue() { return (float) longValue(); }
    public double doubleValue() { return (double) longValue(); }

    public String toString(int radix) {
        StringBuilder b = new StringBuilder();
        write(b, radix);
        return b.toString();
    }

    public void write(StringBuilder b, int radix) {
        int[] cur   = cloneWords();
        int   len   = cur.length,
              size  = len,
              i = b.length(),
              j = i - 1;

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

    public void write(StringBuilder b) {
        write(b, 10);
    }

    public abstract boolean equals(Object o);

    public class Square {
        int[] x;
        int[] y;
        int   len;

        public Square(Atom a, Atom b) {
            int[] aw = a.words(), bw = b.words();
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

    // TODO: make efficient versions of bump, mas, cap for direct atoms
    public Atom bump() {
      return add(ONE);
    }

    public Atom mas() {
      int b = met((byte)0);
      if ( b < 2 ) return null;
      int  c = 1 << (b - 1),
           d = 1 << (b - 2);
      Atom e = sub(fromLong(c));
      return con(e, fromLong(d));
    }

    public int cap() {
      int b = met((byte)0);
      if ( b < 2 ) return -1;
      return bit(b - 2) ? 3 : 2;
    }

    public Atom add(Atom b) {
        Square s   = new Square(this, b);
        int[] dst  = new int[s.len+1];
        dst[s.len] = MPN.add_n(dst, s.x, s.y, s.len);
        return malt(dst);
    }

    public Atom sub(Atom b) {
        Square s  = new Square(this, b);
        int[] dst = new int[s.len];
        int   bor = MPN.sub_n(dst, s.x, s.y, s.len);
        assert bor == 0;
        return malt(dst);
    }

    /* This section is stolen from c3 */
    public abstract boolean bit(int a);

    public int met(byte a) {
        int gal, daz;

        assert a < 32;

        if (isZero()) {
            return 0;
        }

        if (isCat()) {
            gal = 0;
            daz = intValue();
        }
        else {
            IndirectAtom b = (IndirectAtom) this;
            gal = b.words.length - 1;
            daz = b.words[gal];
        }

        switch (a) {
            case 0:
            case 1:
            case 2:
                int col = 32 - Integer.numberOfLeadingZeros(daz),
                    bif = col + (gal << 5);

                return (bif + ((1 << a) - 1) >>> a);

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

            default:
                int gow = a - 5;
                return ((gal + 1) + ((1 << gow) - 1)) >>> gow;
        }
    }

    public static Atom mix(Atom a, Atom b) {
        byte w = 5;
        int lna = a.met(w),
            lnb = b.met(w);

        if (lna == 0 && lnb == 0) {
            return ZERO;
        }

        int   len = Math.max(lna, lnb);
        int[] sal = new int[len];

        chop(w, 0, lna, 0, sal, a);

        int[] bw = b.words();
        for (int i = 0; i < lnb; ++i) {
            sal[i] ^= bw[i];
        }

        return malt(sal);
    }

    public static Atom con(Atom a, Atom b) {
      byte w   = 5;
      int  lna = a.met(w);
      int  lnb = b.met(w);

      if ( (0 == lna) && (0 == lnb) ) {
        return ZERO;
      }
      else {
        int i, len = Math.max(lna, lnb);
        int[] sal  = new int[len];
        int[] bow  = b.words();

        chop(w, 0, lna, 0, sal, a);

        for ( i = 0; i < lnb; i++ ) {
          sal[i] |= bow[i];
        }

        return malt(sal);
      }
    }

    public static void chop(byte met, int fum, int wid, int tou, int[] dst, Atom src) {
        int[] buf = src.words();
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

    public static int[] slaq(int met, int len) {
        return new int[((len << met) + 31) >>> 5];
    }



  private static Noun frag_word(int a, Noun b) {
    int dep = 31 - MPN.count_leading_zeros(a);

    while ( dep > 0 ) {
      if ( !(b instanceof Cell) ) {
        return null;
      }
      else {
        Cell c = (Cell) b;
        b = ( 0 == (1 & (a >>> --dep)) )
          ? c.p
          : c.q;
      }
    }

    return b;
  }

  private static Noun frag_deep(int a, Noun b) {
    int dep = 32;

    while ( dep > 0 ) {
      if ( !(b instanceof Cell) ) {
        return null;
      }
      else {
        Cell c = (Cell) b;
        b = ( 0 == (1 & (a >>> --dep)) )
          ? c.p
          : c.q;
      }
    }

    return b;
  }

  protected static void fragIn(Queue<boolean> q, int a, int dep) {
    while ( dep > 0 ) {
      q.add( 0 != (1 & (a >>> --dep)) );
    }
  }

  protected static void fragIn(Queue<boolean> q, int a) {
    fragIn(q, 31 - MPN.count_leading_zeros(a));
  }

  protected abstract void fragOut(Queue<boolean> q);

  public List<boolean> fragments() {
    if ( this == Atom.ZERO ) {
      return null;
    } 
    else {
      Queue<boolean> q = new Queue<boolean>();
      fragOut(q);
      return q;
    }
  }
}
