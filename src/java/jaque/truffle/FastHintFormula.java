package jaque.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import jaque.interpreter.Bail;
import jaque.noun.Atom;
import jaque.noun.Cell;
import jaque.noun.Noun;

public class FastHintFormula extends UnsafeFormula {
  @Child private Formula clueF;
  @Child private Formula nextF;
  
  public FastHintFormula(Formula clue, Formula next) {
    this.clueF = clue;
    this.nextF = next;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    Object s = getSubject(frame);
    Object c = clueF.executeSafe(frame);
    setSubject(frame, s);
    Object o = nextF.executeSafe(frame);
    if (o instanceof Cell) {
      getContext(frame).declare((Cell) o, c);
    }
    replace(nextF);
    return o;
  }

  @Override
  public Cell toCell() {
    return new Cell(10L, new Cell(new Cell(Atom.FAST, clueF.toCell()), nextF.toCell()));
  }
}
