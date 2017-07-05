package net.frodwith.jaque.data;

import net.frodwith.jaque.Bail;

public final class Tank {
  private static final long SPACE = 32L;

  private static final Object re_ram(Object tac) {
    Cell c = Cell.expect(tac);
    long  tem = Atom.expectLong(c.head);
    
    if ( Atom.LEAF.equals(tem) ) {
      return c.tail;
    }
    else if ( Atom.PALM.equals(tem) ) {
      return re_ram_palm(c.tail);
    }
    else if ( Atom.ROSE.equals(tem) ) {
      return re_ram_rose(c.tail);
    }
    else {
      throw new Bail();
    }
  }
  
  private static final Object re_ram_palm(Object noun) {
    Cell bub = Cell.expect(noun);
    Qual qua = Qual.expect(bub.head);
    Trel pur = new Trel(qua.p, List.weld(qua.q, qua.r), qua.s);
    Cell rob = new Cell(pur.toCell(), bub.tail);

    return re_ram_rose(rob);
  }
  
  private static final Object re_ram_rose(Object noun) {
    Cell bub = Cell.expect(noun);
    Trel tre = Trel.expect(bub.head);
    
    return List.weld(tre.q, re_ram_rose_in(tre.p, tre.r, bub.tail));
  }
  
  private static final Object re_ram_rose_in(Object p, Object r, Object res) {
    if ( Atom.isZero(res) ) {
      return r;
    }
    Cell rec = Cell.expect(res);
    Object voz = re_ram_rose_in(p, r, rec.tail),
           dex = re_ram(rec.head),
           sin = Atom.isZero(rec.tail) ? voz : List.weld(p, voz);
    
    return List.weld(dex, sin);
  }
  
  private static final Object re_win_buc(Object tac, Object tab, Object edg, Object lug) {
    Cell c = Cell.expect(tac);
    long tem = Atom.expectLong(c.head);
    
    if ( Atom.LEAF.equals(tem) ) {
      return re_win_leaf(tac, tab, edg, lug);
    }
    else if ( Atom.PALM.equals(tem) ) {
      return re_win_palm(tac, tab, edg, lug);
    }
    else if ( Atom.ROSE.equals(tem) ) {
      return re_win_rose(tac, tab, edg, lug);
    }
    else {
      throw new Bail();
    }
  }
  
  private static final Object re_win_din(Object tab, Object edg) {
    return Atom.mod(Atom.add(2L, tab), Atom.mul(2L, Atom.div(edg, 3L)));
  }
  
  private static final Object re_win_fit(Object tac, Object tab, Object edg) {
    Object ram = re_ram(tac),
           len = List.lent(ram),
           dif = Atom.sub(edg, tab);
    
    return (Atom.compare(len, dif) != 1) ? Atom.YES : Atom.NO;
  }
  
  private static final Object re_win_leaf(Object tac, Object tab, Object edg, Object lug) {
    Cell c = Cell.expect(tac);
    return re_win_rig(c.tail, tab, lug);
  }
  
  private static final Object re_win_palm(Object tac, Object tab, Object edg, Object lug) {
    if ( re_win_fit(tac, tab, edg).equals(Atom.YES) ) {
      return re_win_rig(re_ram(tac), tab, lug);
    }
    else {
      Cell c = Cell.expect(tac);
      Cell bub = Cell.expect(c.tail);
      Qual qua = Qual.expect(bub.head);
      
      if ( Atom.isZero(bub.tail) ) {
        return re_win_rig(qua.q, tab, lug);
      }
      Cell res = Cell.expect(bub.tail);
      if ( Atom.isZero(res.tail) ) {
        Object bat = Atom.add(2L,  tab),
               gul = re_win_buc(res.head, tab, edg, lug);
        return re_win_rig(qua.q, bat, gul);
      }
      else {
        Object lyn = Atom.mul(2L,  List.lent(res)),
               qyr = re_win_palm_qyr(tab, edg, lyn, res, lug);
        return re_win_wig(qua.q, tab, edg, qyr);
      }
    }
  }
  
  private static final Object re_win_palm_qyr(Object tab, Object edg, Object lyn, Object res, Object lug) {
    if ( Atom.isZero(res) ) {
      return lug;
    }
    Cell c = Cell.expect(res);
    Object cat = c.head,
           sub = Atom.sub(lyn, 2L),
           bat = Atom.add(tab, sub),
           gul = re_win_palm_qyr(tab, edg, sub, c.tail, lug);

    return re_win_buc(cat, bat, edg, gul);
  }

  private static final Object re_win_rig(Object hom, Object tab, Object lug) {
    return new Cell(Tape.runt(tab, SPACE, hom), lug);
  }

  private static final Object re_win_rose(Object tac, Object tab, Object edg, Object lug) {
    Cell c = Cell.expect(tac);
    Cell bub = Cell.expect(c.tail);
    Trel tre = Trel.expect(bub.head);
    
    if ( re_win_fit(tac, tab, edg).equals(Atom.YES) ) {
      return re_win_rig(re_ram(tac), tab, lug);
    }
    else {
      Object gul = re_win_rose_lug(tre.r, tab, edg, bub.tail, lug);
      if ( Atom.isZero(tre.q) ) {
        return gul;
      }
      else {
        return re_win_wig(tre.q, tab, edg, gul);
      }
    }
  }
  
  private static final Object re_win_rose_lug(Object r, Object tab, Object edg, Object res, Object lug) {
    if ( Atom.isZero(res) ) {
      if ( Atom.isZero(r) ) {
        return lug;
      }
      else {
        return re_win_rig(r, tab, lug);
      }
    }
    else {
      Cell c = Cell.expect(res);
      Object cat = c.head,
             gul = re_win_rose_lug(r, tab, edg, c.tail, lug),
             bat = re_win_din(tab, edg);
      return re_win_buc(cat, bat, edg, gul);
    }
  }
  
  private static final Object re_win_wig(Object hom, Object tab, Object edg, Object lug) {
    if ( Atom.isZero(lug) ) {
      return re_win_rig(hom, tab, lug);
    }

    Cell c = Cell.expect(lug);
    Object lin = List.lent(hom),
           wug = Atom.increment(Atom.add(tab, lin));

    if ( re_win_wig_mir(c.head, wug).equals(Atom.NO) ) {
      return re_win_rig(hom, tab, lug);
    }
    else {
      Object sin = new Cell(SPACE, List.slag(wug, c.head)),
             moh = List.weld(hom,  sin),
             dex = Tape.runt(tab, SPACE, moh);
      return new Cell(dex, c.tail);
    }
  }
  
  private static final Object re_win_wig_mir(Object mir, Object wug) {
    if ( Atom.isZero(mir) ) {
      return Atom.NO;
    }
    if ( Atom.isZero(wug) ) {
      return Atom.YES;
    }
    Cell c = Cell.expect(mir);
    if ( c.head.equals(SPACE) ) {
      return Atom.NO;
    }
    return re_win_wig_mir(c.tail, Atom.dec(wug));
  }
  
  public static Object wash(Object tab, Object edg, Object tac) {
    return re_win_buc(tac, tab, edg, 0L);
  }
}
