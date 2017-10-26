package net.frodwith.jaque.data;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;

public class Trel {
  public Object p, q, r;
  
  public Trel(Object p, Object q, Object r) {
    this.p = p;
    this.q = q;
    this.r = r;
  }
  
  public static Trel expect(Object noun) throws UnexpectedResultException {
    Cell h = Cell.expect(noun),
         t = Cell.expect(h.tail);
    return new Trel(h.head, t.head, t.tail);
  }
  
  public static Trel orBail(Object noun) {
    try {
      return expect(noun);
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }
  
  public Cell toCell() {
    return new Cell(p, new Cell(q, r));
  }
}
