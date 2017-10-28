package net.frodwith.jaque.data;

import java.util.Iterator;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.truffle.TypesGen;

public final class Axis implements Iterable<Fragment> {
  public final int length;
  public final Object atom;

  public class Cursor implements Iterator<Fragment> {
    private int n;
    
    public Cursor() {
      this.n = length - 1;
    }

    @Override
    public boolean hasNext() {
      return n >= 0;
    }

    @Override
    public Fragment next() {
      return Atom.getNthBit(atom, n--) ? Fragment.TAIL : Fragment.HEAD;
    }

  }
  
  public Axis(Object atom) {
    assert !Atom.isZero(atom);
    assert !Atom.equals(atom, 1L);
    this.atom = atom;
    this.length = Atom.met(atom) - 1;
  }

  
  public Object fragOrBail(Object a) {
    try {
      return fragment(a);
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }

  public Object fragment(Object a) throws UnexpectedResultException {
    for ( Fragment f : this ) {
      Cell c = TypesGen.expectCell(a);
      a = ( Fragment.HEAD == f ) ? c.head : c.tail;
    }
    return a;
  }

  @Override
  public Iterator<Fragment> iterator() {
    return new Cursor();
  }
}