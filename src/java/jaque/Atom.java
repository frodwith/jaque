package jaque;

import gnu.math.MPN;

public class Atom {
    public final int   size;
    public final int[] words;

    public static final Atom ZERO = new Atom(1, new int[]{0});

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
        return buf.toString();
    }

    public Atom(String s) {
        this(s, 10);
    }

    /* This section is just stolen from c3 */

    public void chop(int met, int fum, int wid, int tou, int[] dst) {
        int   len = size, i;
        int[] buf = words;

        if (met < 5) {
            int san = 1 << met,
                mek = ((1 << san) - 1),
                baf = fum << met,
                bat = tou << met;

            for (i = 0; i < wid; ++i) {
                int waf = baf >> 5,
                    raf = baf & 31,
                    wat = bat >> 5,
                    rat = bat & 31,
                    hop;

                hop = (waf >= len) ? 0 : buf[waf];
                hop = (hop >> raf) & mek;

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
        return new int[((len << met) + 31) >> 5];
    }

    public int met(byte a) {
        int gal = size - 1,
            daz = words[gal];

        switch (a) {
            case 0:
            case 1:
            case 2:
                int col = Long.numberOfLeadingZeros(daz),
                    bif = col + (gal << 5);

                return (bif + ((1 << a) - 1) >> a);

            case 3:
                return (gal << 2)
                    + ((daz >> 24 != 0)
                       ? 4
                       : (daz >> 16 != 0)
                       ? 3
                       : (daz >> 8 != 0)
                       ? 2
                       : 1);

            case 4:
                return (gal << 1) + ((daz >> 16 != 0) ? 2 : 1);

            default:
                int gow = a - 5;
                return ((gal + 1) + ((1 << gow) - 1)) >> gow;
        }
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

        return new Atom(sal.length, sal);
    }
}
