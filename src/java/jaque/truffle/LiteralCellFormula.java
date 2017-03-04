package jaque.truffle;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import jaque.noun.Cell;

public class LiteralCellFormula extends LiteralFormula {
  private final Cell c;

  public LiteralCellFormula(Cell value) {
    this.c = value;
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    return executeCell(frame);
  }
  
  @Override
  public Cell executeCell(VirtualFrame f) {
    return c;
  }
  
  public Object getValue() {
    return c;
  }
}
