package jaque.truffle;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

public class LiteralLongFormula extends LiteralFormula {
  private final long value;

  public LiteralLongFormula(long value) {
    this.value = value;
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    return executeLong(frame);
  }
  
  @Override
  public long executeLong(VirtualFrame f) {
    return value;
  }
  
  public Object getValue() {
    return value;
  }
}
