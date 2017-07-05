package net.frodwith.jaque.data;

public class Qual {
  public Object p, q, r, s;
  
  private Qual(Object p, Object q, Object r, Object s) {
    this.p = p;
    this.q = q;
    this.r = r;
    this.s = s;
  }
  
  public static Qual expect(Object noun) {
    Cell c = Cell.expect(noun);
    Trel t = Trel.expect(c.tail);
    
    return new Qual(c.head, t.p, t.q, t.r);
  }
}
