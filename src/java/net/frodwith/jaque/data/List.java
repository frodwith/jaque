package net.frodwith.jaque.data;

import java.util.Iterator;
import java.util.Stack;
import java.util.function.Function;

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
  
  public static Object reel(Function<Object,Object> f, Object seed, Object list) {
    Stack<Object> s = new Stack<Object>();
    for ( Object i : new List(list) ) {
      s.push(i);
    }
    while ( !s.empty() ) {
      seed = f.apply(new Cell(s.pop(), seed));
    }
    return seed;
  }
  
  public static Object roll(Function<Object,Object> f, Object seed, Object list) {
    for ( Object i : new List(list) ) {
      seed = f.apply(new Cell(i, seed));
    }
    return seed;
  }
  
  public static Object turn(Function<Object,Object> f, Object list) {
    Stack<Object> s = new Stack<Object>();
    for ( Object i : new List(list) ) {
      s.push(i);
    }
    Object r = 0L;
    while ( !s.empty() ) {
      r = new Cell(f.apply(s.pop()), r);
    }
    return r;
  }

  public static Object weld(Object a, Object b) {
    Stack<Object> s = new Stack<Object>();
    for ( Object i : new List(a) ) {
      s.push(i);
    }
    Object r = b;
    while ( !s.empty() ) {
      r = new Cell(s.pop(), r);
    }
    return r;
  }
  
  public static boolean lien(Function<Object,Object> f, Object list) {
    for ( Object i : new List(list) ) {
      if ( Atom.equals(Atom.YES, f.apply(i)) ) {
        return true;
      }
    }
    return false;
  }

  public static boolean levy(Function<Object,Object> f, Object list) {
    for ( Object i : new List(list) ) {
      if ( Atom.equals(Atom.NO, f.apply(i)) ) {
        return false;
      }
    }
    return true;
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
  
  // longest common subsequence
  // credit to: https://rosettacode.org/wiki/Longest_common_subsequence#Dynamic_Programming_3
  public static Object loss(Object a, Object b) {
    int i, j, lea = Atom.expectInt(lent(a)), leb = Atom.expectInt(lent(b));
    int[][] lens = new int[lea+1][leb+1];
    Object[] aa = new Object[lea], bb = new Object[leb];
    
    i = 0;
    for ( Object o : new List(a) ) {
      aa[i++] = o;
    }
    
    i = 0;
    for ( Object o : new List(b) ) {
      bb[i++] = o;
    }
    
    for ( i = 0; i < lea; ++i ) {
      for ( j = 0; j < leb; ++j ) {
        if ( Noun.equals(aa[i], bb[j]) ) {
          lens[i+1][j+1] = lens[i][j] + 1;
        }
        else {
          lens[i+1][j+1] = Math.max(lens[i+1][j], lens[i][j+1]);
        }
      }
    }
    
    Object r = 0L;
    for ( i = lea, j = leb;
          i != 0 && j != 0; ) {
      if ( lens[i][j] == lens[i-1][j] ) {
        --i;
      }
      else if ( lens[i][j] == lens[i][j-1] ) {
        --j;
      }
      else {
        assert aa[i-1] == bb[j-1];
        --i; --j;
        r = new Cell(aa[i],r);
      }
    }
    return r;
  }
}
