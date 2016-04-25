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
        return val;
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

    public boolean isCat() {
        return true;
    }
}
