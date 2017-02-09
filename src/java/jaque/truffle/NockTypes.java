package jaque.truffle;

import jaque.noun.*;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.dsl.TypeSystem;
import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;

@TypeSystem({boolean.class, long.class, Atom.class, Cell.class, Noun.class})
public abstract class NockTypes {

  @ImplicitCast
  public static long castLong(boolean value) {
    return value ? 0 : 1;
  }

  @ImplicitCast
  public static Atom castAtom(long value) {
    return Atom.fromLong(value);
  }

  @TypeCast(Boolean.class)
  public static boolean asBoolean(Noun value) throws UnexpectedResultException {
    if ( value == Atom.YES ) {
      return true;
    }
    else if ( value == Atom.NO ) {
      return false;
    }
    else {
      throw new UnexpectedResultException(value);
    }
  }
}
