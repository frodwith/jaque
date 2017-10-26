package net.frodwith.jaque.data;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class Qual {
  public Object p, q, r, s;
  
  public Qual(Object p, Object q, Object r, Object s) {
    this.p = p;
    this.q = q;
    this.r = r;
    this.s = s;
  }
  
  public static Qual expect(Object noun) throws UnexpectedResultException {
    Cell c = Cell.expect(noun);
    Trel t = Trel.expect(c.tail);
    return new Qual(c.head, t.p, t.q, t.r);
  }
  
  
  public static Qual orBail(Object noun) {
    Cell c = Cell.orBail(noun);
    Trel t = Trel.orBail(c.tail);
    
    return new Qual(c.head, t.p, t.q, t.r);
  }

  public Cell toCell() {
    return new Cell(p, new Trel(q, r, s).toCell());
  }
}
