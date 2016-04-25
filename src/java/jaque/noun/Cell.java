package jaque.noun;

public class Cell extends Noun {
    public final Noun p;
    public final Noun q;
    public boolean hashed;
    public int hash;

    public Cell(Noun p, Noun q) {
        this.p = p;
        this.q = q;
    }

    public void write(StringBuilder b) {
        b.append("[");
        p.write(b);
        b.append(" ");
        q.write(b);
        b.append("]");
    }

    public final int hashCode() {
        if (!hashed) {
            hash = p.hashCode() ^ q.hashCode();
            hashed = true;
        }
        return hash;
    }

    public final boolean equals(Object o) {
        if (!(o instanceof Cell)) {
            return false;
        }
        Cell c = (Cell) o;
        return (hashCode() == c.hashCode()) && p.equals(c.p) && q.equals(c.q);
    }
}
