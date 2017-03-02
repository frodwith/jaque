package jaque.truffle;

import com.oracle.truffle.api.dsl.Specialization;

public abstract class LiteralLongFormula extends LiteralFormula {
  private final long value;

  public LiteralLongFormula(long value) {
    this.value = value;
  }
  
  @Specialization
  public long value() {
    return value;
  }
  
  public Object getValue() {
    return value;
  }
}
