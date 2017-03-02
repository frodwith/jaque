package jaque.noun;

import clojure.lang.ISeq;
import clojure.lang.IteratorSeq;
import java.util.LinkedList;

public class Cell extends Noun {
  private final Object p;
  private final Object q;
  public boolean hashed;
  public int hash;
  
  public Cell(Object head, Object tail) {
    assert head != null;
    assert tail != null; 
    this.p = head;
    this.q = tail;
  }

  public Cell(Noun p, Noun q) {
    this((Object) p, (Object) q);
  }

  public Noun p() {
    return coerceNoun(p);
  }
  
  public Noun q() {
    return coerceNoun(q);
  }
  
  public Object getHead() {
    return p;
  }
  
  public Object getTail() {
    return q;
  }

  public void write(StringBuilder b) {
    b.append("[");
    p().write(b);
    b.append(" ");
    q().write(b);
    b.append("]");
  }
  
  public ISeq seq() {
      ISeq head = p().seq();
      ISeq tail = q().seq();
      LinkedList<Noun> l = new LinkedList<Noun>();

      do {
          l.add((Noun) head.first());
      } while ((head = head.next()) != null);

      do  {
          l.add((Noun) tail.first());
      } while ((tail = tail.next()) != null);

      return IteratorSeq.create(l.iterator());
  }

  public boolean isZero() {
    return false;
  }

  public final int hashCode() {
    if (!hashed) {
        hash = Noun._mug_both(p.hashCode(), q.hashCode());
        hashed = true;
    }
    return hash;
  }

  public final boolean equals(Object o) {
    if (o == this) {
        return true;
    }
    if (!(o instanceof Cell)) {
        return false;
    }
    Cell c = (Cell) o;
    if (hashed && c.hashed && hash != c.hash) {
        return false;
    }
    else {
        return p.equals(c.p) && q.equals(c.q);
    }
  }
}
