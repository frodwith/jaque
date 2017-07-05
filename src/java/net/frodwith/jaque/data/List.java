package net.frodwith.jaque.data;

import java.util.Iterator;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.truffle.TypesGen;

public class List implements Iterable<Object> {
  private Object noun;
  
  public List(Object noun) {
    this.noun = noun;
  }

  @Override
  public Iterator<Object> iterator() {
    return new Cursor(noun);
  }

  public class Cursor implements Iterator<Object> {
    private Object cur;
    
    public Cursor(Object noun) {
      this.cur = noun;
    }

    @Override
    public boolean hasNext() {
      return Noun.isCell(cur);
    }

    @Override
    public Object next() {
      try {
        Cell c = TypesGen.expectCell(cur);
        cur = c.tail;
        return c.head;
      }
      catch (UnexpectedResultException e) {
        throw new Bail();
      }
    }
  }

  public static Object weld(Object dex, Object sin) {
    if ( Atom.isZero(dex) ) {
      return sin;
    }
    Cell c = Cell.expect(dex);
    return new Cell(c.head, weld(c.tail, sin));
  }

  public static Object lent(Object ram) {
    Object i = 0;
    for ( Object x : new List(ram) ) {
      i = Atom.increment(i);
    }
    return i;
  }

  public static Object slag(Object a, Object b) {
    while ( !Atom.isZero(a) ) {
      if ( !Noun.isCell(b) ) {
        return 0L;
      }
      b = Cell.expect(b).tail;
      a = Atom.dec(a);
    }
    return b;
  }
}
