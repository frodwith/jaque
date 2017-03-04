package jaque.truffle;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import jaque.noun.Atom;

public class LiteralBooleanFormula extends LiteralFormula {
  private final boolean b;
  
  public LiteralBooleanFormula(boolean value) {
    this.b = value;
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    return executeBoolean(frame);
  }
  
  @Override
  public boolean executeBoolean(VirtualFrame f) {
    return b;
  }
  
  
  public Object getValue() {
    return b;
  }
}
