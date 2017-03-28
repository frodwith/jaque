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
}
