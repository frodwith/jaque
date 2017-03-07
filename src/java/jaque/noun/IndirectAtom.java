package jaque.noun;

import gnu.math.MPN;
import java.util.Queue;

public class IndirectAtom extends Atom {
    public final int[] words;
    public boolean hashed;
    public int hash;

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

    public boolean equals(Object o) {
      if ( (o instanceof Long) && words.length <= 2 ) {
        return longValue() == (long) o;
      }
      else if ( o instanceof IndirectAtom ) {
        IndirectAtom a = (IndirectAtom) o;
        return (hashCode() == a.hashCode())
            || (0 == compareTo(a));
      }
      else {
        return false;
      }
    }

    public int hashCode() {
        if (!hashed) {
            hash = Noun._mug_words((int) 2166136261L, words.length, words);
            hashed = true;
        }
        return hash;
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

    public int lo() {
        return words[0];
    }

    public boolean isCat() {
        return false;
    }

    public boolean bit(int a) {
      int pix = a >> 5;

      if ( pix >= words.length ) {
        return false;
      }
      else {
        return (1 & (words[pix] >>> (a & 31))) > 0;
      }
    }

    protected void fragOut(Queue<Boolean> q) {
      int len = words.length;

      fragIn(q, words[--len]);
      while ( len > 0 ) {
        fragIn(q, words[--len], 32);
      }
    }
}
