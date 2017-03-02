package jaque.truffle;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import jaque.noun.Cell;

public abstract class LiteralCellFormula extends LiteralFormula {
  private final Cell c;

  public LiteralCellFormula(Cell value) {
    this.c = value;
  }
  
  @Specialization
  public Cell cell() {
    return c;
  }
  
  public Object getValue() {
    return c;
  }
}
