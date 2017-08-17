package net.frodwith.jaque.data;

import java.util.function.Function;

public class Parse {

  public static Object compFun(Function<Object,Object> raq, Cell edge, Function<Object,Object> rule) {
    if ( Noun.isAtom(edge.tail) ) {
      return edge;
    }
    else {
      Cell qvex = Cell.expect(edge.tail);
      Cell uqvex = Cell.expect(qvex.tail);
      Cell yit = Cell.expect(rule.apply(uqvex.tail));
      Object yur = last(Cell.expect(edge.head), Cell.expect(yit.head));

      if ( Noun.isAtom(yit.tail) ) {
        return new Cell(yur, 0L);
      }
      else {
        Cell uqyit = Cell.expect(Cell.expect(yit.tail).tail);
        Object thr = raq.apply(new Cell(uqvex.head, uqyit.head));
        return new Qual(yur, 0L, thr, uqyit.tail).toCell();
      }
    }
  }

  private static Object last(Cell zyc, Cell naz) {
    long pzyc = Atom.expectLong(zyc.head),
         qzyc = Atom.expectLong(zyc.tail),
         pnaz = Atom.expectLong(naz.head),
         qnaz = Atom.expectLong(naz.tail);

    return (pzyc == pnaz)
        ? ((Atom.compare(qzyc, qnaz) == 1) ? zyc : naz)
        : ((Atom.compare(pzyc, pnaz) == 1) ? zyc : naz);
  }

}
