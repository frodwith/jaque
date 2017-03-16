package net.frodwith.jaque.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.truffle.TypesGen;

public class Shape {
  private static final Map<PairKey, Shape> registry;
  public final Object[] axes;
  public final static Shape ATOM;
  
  static {
    ATOM = new Shape(new Object[] { 1L });
    registry = new HashMap<PairKey, Shape>();
  }
  
  private Shape(Object[] axes) {
    this.axes = axes;
  }
  
  @TruffleBoundary
  public static Shape cons(Shape left, Shape right) {
    PairKey k = new PairKey(left, right);
    if ( registry.containsKey(k) ) {
      return registry.get(k);
    }
    else {
      Object[] axes = new Object[left.axes.length + right.axes.length + 1];
      int i = 0;
      axes[i++] = 1L;
      for ( Object axis : left.axes ) {
        axes[i++] = Atom.peg(axis, 2L);
      }
      for ( Object axis : right.axes ) {
        axes[i++] = Atom.peg(axis, 3L);
      }
      Shape s = new Shape(axes);
      registry.put(k, s);
      return s;
    }
  }
  
  public int hashCode() {
    return Arrays.hashCode(axes);
  }
  
  public boolean equals(Object o) {
    return this == o;
  }
  
  public static Shape forNoun(Object noun) {
    if ( TypesGen.isCell(noun) ) {
      return TypesGen.asCell(noun).shape;
    }
    else {
      return ATOM;
    }
  }
  
  private static final class PairKey {
    public final Shape left;
    public final Shape right;
    
    public PairKey(Shape left, Shape right) {
      this.left = left;
      this.right = right;
    }
    
    public boolean equals(Object o) {
      if ( o instanceof PairKey ) {
        PairKey k = (PairKey) o;
        return k.left == left && k.right == right;
      }
      else {
        return false;
      }
    }
    
    public int hashCode() {
      return left.hashCode() ^ right.hashCode();
    }
  }
}