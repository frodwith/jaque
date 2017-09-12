package net.frodwith.jaque.data;

import java.util.Iterator;
import java.util.Stack;

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

  public static Object weld(Object a, Object b) {
    Stack<Object> s = new Stack<Object>();
    for ( Object i : new List(a) ) {
      s.push(i);
    }
    Object r = b;
    while ( !s.isEmpty() ) {
      r = new Cell(s.pop(), r);
    }
    return r;
  }

  public static Object lent(Object ram) {
    Iterator<Object> i = new List(ram).iterator();
    Iterator<Object> c = new Atom.Counter();
    Object r = 0L;
    
    while ( i.hasNext() ) {
      r = c.next();
      i.next();
    }

    return r;
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

  public static Object flop(Object a) {
    Object r = 0L;
    for ( Object i : new List(a) ) {
      r = new Cell(i, r);
    }
    return r;
  }
}
