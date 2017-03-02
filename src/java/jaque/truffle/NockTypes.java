package jaque.truffle;

import jaque.noun.*;
import com.oracle.truffle.api.dsl.TypeSystem;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;

@TypeSystem({boolean.class, long.class, Atom.class, Cell.class})
public abstract class NockTypes {
  
  @TypeCheck(boolean.class)
  public static boolean isBoolean(Object value) {
    if ( value == Atom.YES || value == Atom.NO ) {
      return true;
    }
    else if ( value instanceof Boolean ) {
      return true;
    }
    else if ( value instanceof Long ) {
      long l = (long) value;
      return l == 0L || l == 1L;
    }
    else {
      return false;
    }
  }
  
  @TypeCast(boolean.class)
  public static boolean asBoolean(Object value) {
    if ( value == Atom.YES ) {
      return true;
    }
    else if ( value == Atom.NO ) {
      return false;
    }
    else if ( value instanceof Long ) {
      return (long) value == 0L;
    }
    else {
      return (boolean) value;
    }
  }
  
  @TypeCheck(long.class)
  public static boolean isLong(Object value) {
    if ( value instanceof Boolean || value instanceof Long || value instanceof DirectAtom) {
      return true;
    }
    else if ( value instanceof IndirectAtom ) {
      return ((IndirectAtom) value).words.length <= 2;
    }
    else {
      return false;
    }
  }
  
  @TypeCast(long.class)
  public static long asLong(Object value) {
    if ( value instanceof Boolean ) {
      if ((boolean) value) {
        return 0L;
      }
      else {
        return 1L;
      }
    }
    else if ( value instanceof Long ) {
      return (long) value;
    }
    else {
      return ((Atom) value).longValue();
    }
  }
  
  @TypeCheck(Atom.class)
  public static boolean isAtom(Object value) {
    return (value instanceof Boolean)
        || (value instanceof Long)
        || (value instanceof Atom);
  }

  @TypeCast(Atom.class)
  public static Atom asAtom(Object value) {
    return Atom.coerceAtom(value);
  }
}
