package jaque.noun;

import clojure.lang.ISeq;
import clojure.lang.IteratorSeq;
import java.util.LinkedList;

public class Cell extends Noun {
    public final Noun p;
    public final Noun q;
    public boolean hashed;
    public int hash;

    public ISeq seq() {
        ISeq head = p.seq();
        ISeq tail = q.seq();
        LinkedList<Noun> l = new LinkedList<Noun>();

        do {
            l.add((Noun) head.first());
        } while ((head = head.next()) != null);

        do  {
            l.add((Noun) tail.first());
        } while ((tail = tail.next()) != null);

        return IteratorSeq.create(l.iterator());
    }

    public Cell(Noun p, Noun q) throws Exception {
        if (p == null) {
            throw new Exception("Null p in cell constructor");
        }
        if (q == null) {
            throw new Exception("Null q in cell constructor");
        }
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
            hash = Noun._mug_both(p.hashCode(), q.hashCode());
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
