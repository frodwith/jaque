package jaque.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import jaque.interpreter.Bail;
import jaque.noun.Cell;
import jaque.noun.Noun;

public class FastHintFormula extends Formula {
  @Child private Formula nextF;
  @Child private Formula clueF;
  
  public FastHintFormula(Formula next, Formula clue) {
    this.nextF = next;
    this.clueF = clue;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    Object c = clueF.execute(frame);
    Object o = nextF.execute(frame);
    if (o instanceof Cell) {
      getContext(frame).declare((Cell) o, c);
    }
    return o;
  }

  @Override
  public Cell toCell() {
    return new Cell(10, new Cell(new Cell(FAST, clueF.toCell()), nextF.toCell()));
  }
}
