package jaque.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import jaque.interpreter.Bail;
import jaque.noun.Atom;
import jaque.noun.Cell;
import jaque.noun.Noun;

public class FastHintFormula extends SafeFormula {
  @Child private Formula nextF;
  @Child private Formula clueF;
  
  public FastHintFormula(Formula next, Formula clue) {
    this.nextF = next;
    this.clueF = clue;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    Object c = clueF.executeSafe(frame);
    Object o = nextF.executeSafe(frame);
    if (o instanceof Cell) {
      getContext(frame).declare((Cell) o, c);
    }
    return o;
  }

  @Override
  public Cell toCell() {
    return new Cell(10L, new Cell(new Cell(Atom.FAST, clueF.toCell()), nextF.toCell()));
  }
}
