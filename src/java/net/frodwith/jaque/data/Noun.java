package net.frodwith.jaque.data;

import java.util.Arrays;

import net.frodwith.jaque.truffle.Bail;

/* This is not the base class for nock objects, because nock objects are Objects.
 * This is where we put static library methods that operate on all nouns.
 * 
 * For type-cast/check and coercion operations, please use the package
 * net.frodwith.jaque.truffle.TypesGen. The generic atom type is unfortunately
 * named ImplicitIntArray, eg. instead of TypesGen.isImplicitIntArray(). Sorry.
 */
public class Noun {
  
  public static boolean equals(Object a, Object b) {
    if ( (a instanceof Long) && (b instanceof Long) ) {
      return a == b;
    }
    else if ( (a instanceof int[]) && (b instanceof int[]) ) {
      return Arrays.equals((int[]) a, (int[]) b);
    }
    else if ( (a instanceof Cell) && (b instanceof Cell) ) {
      return Cell.equals((Cell) a, (Cell) b);
    }
    else {
      return false;
    }
  }
  
  public static int mug(Object noun) {
    if ( noun instanceof Cell) {
      return Cell.mug((Cell) noun);
    }
    else {
      return Atom.mug(noun);
    }
  }

  /* used by both atom and cell mug methods, so package scope */
  static int mug_fnv(int has) {
    return (has * ((int)16777619));
  }
  
  static int mug_out(int has) {
    return (has >>> 31) ^ (has & 0x7fffffff);
  }
  
  public static Object fragment(Object axis, Object subject) {
    try {
      while ( !Atom.equals(axis, 1L) ) {
        Cell c = (Cell) subject; 
        subject = Atom.cap(axis) == 2L ? c.head : c.tail;
        axis = Atom.mas(axis);
      }
      return subject;
    }
    catch (ClassCastException e) {
      throw new Bail();
    }
  }
}
