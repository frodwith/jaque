package jaque.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;

import jaque.interpreter.Bail;
import jaque.noun.Cell;

public class BailNode extends SafeFormula {
  static final Cell bailCell = new Cell(0L, 0L);
  
  public BailNode() {
  }

  @Override
  public Cell toCell() {
    return bailCell;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    throw new Bail();
  }

}
