package jaque.truffle;

import com.oracle.truffle.api.dsl.Specialization;

public abstract class LiteralBooleanFormula extends LiteralFormula {
  private final boolean b;
  
  public LiteralBooleanFormula(boolean value) {
    this.b = value;
  }
  
  @Specialization
  public boolean bool() {
    return b;
  }
  
  public Object getValue() {
    return b;
  }
}
