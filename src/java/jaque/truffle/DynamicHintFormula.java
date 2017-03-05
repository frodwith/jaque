package jaque.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;

import jaque.interpreter.Hint;
import jaque.noun.Atom;
import jaque.noun.Cell;
import jaque.noun.Noun;

public final class DynamicHintFormula extends HintFormula {
  @Child private Formula hintF;
  @Child private Formula nextF;
  private final Atom kind;

  public DynamicHintFormula(Atom kind, Formula hintF, Formula nextF) {
    super(nextF);
    this.kind  = kind;
    this.hintF = hintF;
    this.nextF = nextF;
  }
  
  public Hint executeHint(VirtualFrame frame) {
    Object subject = getSubject(frame);
    Hint h = new Hint(kind, hintF.executeSafe(frame), subject, source());
    setSubject(frame, subject);
    return h;
  }
  
  public Object executeNext(VirtualFrame frame) {
    return nextF.executeSafe(frame);
  }

  @Override
  public Cell toCell() {
    return new Cell(10L, new Cell(hintF.toCell(), nextF.toCell()));
  }
}
