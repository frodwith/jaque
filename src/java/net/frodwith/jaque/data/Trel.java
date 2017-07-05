package net.frodwith.jaque.data;

public class Trel {
  public Object p, q, r;
  
  public Trel(Object p, Object q, Object r) {
    this.p = p;
    this.q = q;
    this.r = r;
  }
  
  public static Trel expect(Object noun) {
    Cell h = Cell.expect(noun),
         t = Cell.expect(h.tail);
    return new Trel(h.head, t.head, t.tail);
  }
  
  public Cell toCell() {
    return new Cell(p, new Cell(q, r));
  }
}
