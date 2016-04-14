package jaque.noun;

import gnu.math.MPN;
import java.util.Arrays;

public class IndirectAtom extends Atom {
    public final int[] words;

    public IndirectAtom(int[] ws) {
        words = ws;
    }

    public int intValue() {
        return words[0];
    }

    public long longValue() {
        long v = words[0];
        v ^= words[1] << 32;
        return v;
    }

    public int[] cloneWords() {
        return words.clone();
    }

    public int[] words() {
        return words;
    }

    public int hashCode() {
        return Arrays.hashCode(words);
    }

    public int compareTo(Atom b) {
        if (b.isCat()) {
            return 1;
        }
        int[] bw = b.words();
        return MPN.cmp(words, words.length, bw, bw.length);
    }

    public boolean isZero() {
        return false;
    }

    public boolean isCat() {
        return false;
    }
}
