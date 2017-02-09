package jaque.noun;

public class DirectAtom extends Atom {
    public final int val;

    public DirectAtom(int i) {
        val = i;
    }

    public int intValue() {
        return val;
    }

    public long longValue() {
        return (long) val;
    }

    public int[] cloneWords() {
        return words();
    }

    public int[] words() {
        return new int[]{val};
    }

    public int hashCode() {
        return Noun._mug_words((int) 2166136261L, (val > 0 ? 1 : 0), words());
    }

    public boolean equals(Object o) {
        if (!(o instanceof DirectAtom)) {
            return false;
        }
        DirectAtom a = (DirectAtom) o;
        return val == a.val;
    }

    public int compareTo(Atom b) {
        if (b.isCat()) {
            int dif = val - b.intValue();
            return dif == 0 
                ? 0
                : dif > 0
                ? 1
                : -1;
        }

        return -1;
    }

    public boolean isZero() {
        return val == 0;
    }

    public int lo() {
        return val;
    }

    public boolean isCat() {
        return true;
    }

    public boolean bit(int a) {
      if ( a >= 31 ) {
        return false;
      }
      else {
        return (1 & (val >>> a)) > 0;
      }
    }

    protected void fragOut(Queue<boolean> q) {
        return fragIn(q, val);
    }
}
