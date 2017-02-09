package jaque.truffle;

import jaque.noun.*;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@TypeSystem(boolean.class, long.class, Atom.class, Cell.class, Noun.class)
public class NockTypes {

  @ImplicitCast
  public long castLong(boolean value) {
    return value ? 0 : 1;
  }

  @ImplicitCast
  public Atom castAtom(boolean value) {
    return value ? Atom.YES : Atom.NO;
  }

  @ImplicitCast
  public Atom castAtom(long value) {
    return Atom.fromLong(value);
  }

  @TypeCast
  public boolean asBoolean(Noun value) {
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
