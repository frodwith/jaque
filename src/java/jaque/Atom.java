package jaque;

import gnu.math.MPN;
import java.util.Arrays;

public class Atom extends Number implements Comparable<Atom> {
    public final int   size;
    public final int[] words;

    public static final Atom ZERO = malt(new int[]{0});

    public Atom(int size, int[] words) {
        this.size = size;
        this.words = words;
    }

    public Atom(String s, int radix) {
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
        size = siz;
        words = wor;
    }

    public Atom(String s) {
        this(s, 10);
    }

    public Atom(long v) {
        if (v > Integer.MAX_VALUE) {
            words = new int[2];
            words[1] = (int) (v >>> 32);
            size = 2;
        }
        else {
            words = new int[1];
            size = 1;
        }
        words[0] = (int) v;
    }

    public byte byteValue() { return (byte) words[0]; }
    public short shortValue() { return (short) words[0]; }
    public int intValue() { return words[0]; }
    public float floatValue() { return (float) words[0]; }
    public double doubleValue() { return (double) words[0]; }
    public long longValue() {
        long v = words[0];
        if (size > 1) {
            v += words[1] << 32;
        }
        return v;
    }

    public String toString() {
        return toString(10);
    }

    public String toString(int radix) {
        int[] cur = words.clone();
        int   len = size;
        StringBuffer buf = new StringBuffer();
        for(;;) {
            int dig = MPN.divmod_1(cur, cur, size, radix);
            buf.append(Character.forDigit(dig, radix));
            if (cur[len-1] == 0) {
                if (--len == 0) {
                    break;
                }
            }
        }

        buf.reverse();
        String s = buf.toString().replaceFirst("^0+", "");
        return s.length() == 0 ? "0" : s;
    }

    public int hashCode() {
        return Arrays.hashCode(words);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Atom)) {
            return false;
        }
        return 0 == compareTo((Atom) o);
    }

    public int compareTo(Atom b) {
        return MPN.cmp(words, size, b.words, b.size);
    }

    public class Square {
        int[] x;
        int[] y;
        int   len;

        public Square(Atom a, Atom b) {
            if (a.size > b.size) {
                len = a.size;
                x   = a.words;
                y   = new int[len];
                System.arraycopy(b.words, 0, y, 0, b.size);
            }
            else if (a.size < b.size) {
                len = b.size;
                x   = new int[len];
                y   = b.words;
                System.arraycopy(a.words, 0, x, 0, a.size);
            }
            else {
                len = size;
                x   = a.words;
                y   = b.words;
            }
        }
    }

    public Atom add(Atom b) {
        Square s   = new Square(this, b);
        int[] dst  = new int[s.len+1];
        int   car  = MPN.add_n(dst, s.x, s.y, s.len);
        dst[s.len] = car;
        return new Atom(car > 0 ? s.len + 1 : s.len, dst);
    }

    public Atom sub(Atom b) {
        Square s  = new Square(this, b);
        int[] dst = new int[s.len];
        int   bor = MPN.sub_n(dst, s.x, s.y, s.len);
        assert bor == 0;
        return malt(dst);
    }

    public boolean isZero() {
        return size == 1 && words[0] == 0;
    }

    public int lot() {
        return isZero() ? 0 : MPN.findLowestBit(words);
    }

    /* This section is stolen from c3 */

    public Atom lsh(byte a, int b) {
        int len = met(a);

        if (0 == len) {
            return ZERO;
        }

        int lus = b + len;
        assert lus > len;

        int[] sal = slaq(a, lus);
        chop(a, 0, len, b, sal);
        return malt(sal);
    }

    public Atom rsh(byte a, int b) {
        int len = met(a);

        if (b >= len) {
            return ZERO;
        }

        int[] sal = slaq(a, len - b);
        chop(a, b, len - b, 0, sal);
        return malt(sal);
    }

    public void chop(byte met, int fum, int wid, int tou, int[] dst) {
        int   len = size, i;
        int[] buf = words;

        assert met < 32;

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

    public static Atom malt(int[] w) {
        return new Atom(w.length, w);
    }

    public int met(byte a) {
        assert a < 32;
        int gal = size - 1,
            daz = words[gal];

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

    public static Atom cat(byte a, Atom b, Atom c) {
        int lew = b.met(a),
            ler = c.met(a),
            all = lew + ler;

        if (0 == all) {
            return ZERO;
        }

        int[] sal = slaq(a, all);
        b.chop(a, 0, lew, 0, sal);
        c.chop(a, 0, ler, lew, sal);

        return malt(sal);
    }

    public Atom mix(Atom b) {
        byte w = 5;
        int lna = met(w),
            lnb = b.met(w);

        if (lna == 0 && lnb == 0) {
            return ZERO;
        }

        int   len = Math.max(lna, lnb);
        int[] sal = new int[len];

        chop(w, 0, lna, 0, sal);

        for (int i = 0; i < lnb; ++i) {
            sal[i] ^= b.words[i];
        }

        return malt(sal);
    }

    public Atom end(byte a, int b) {
        int len = met(a);

        if (0 == b) {
            return ZERO;
        }
        if (b >= len) {
            return this;
        }

        int[] sal = slaq(a, b);
        chop(a, 0, b, 0, sal);

        return malt(sal);
    }

    public Atom cut(byte a, int b, int c) {
        int len = met(a);

        if ((0 == c) || (b >= len)) {
            return ZERO;
        }

        if (b + c > len) {
            c = len - b;
        }

        if ((0 == b) && (c == len)) {
            return this;
        }

        int[] sal = slaq(a, c);
        chop(a, b, c, 0, sal);

        return malt(sal);
    }
}
