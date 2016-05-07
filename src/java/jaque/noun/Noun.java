package jaque.noun;

import clojure.lang.Seqable;

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
}
