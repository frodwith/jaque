package jaque.truffle;

import jaque.noun.Atom;
import com.oracle.truffle.api.dsl.Specialization;


public abstract class LiteralAtomFormula extends LiteralFormula {
  private final Atom a;

  public LiteralAtomFormula(Atom value) {
    this.a = value;
  }
  
  @Specialization
  public Atom atom() {
    return a;
  }
  
  public Object getValue() {
    return a;
  }
}
