package jaque.truffle;

import jaque.interpreter.Hint;
import jaque.noun.*;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class HintFormula extends Formula {
  protected Atom kind;

  public abstract Noun clue(VirtualFrame frame);
  public abstract Formula next();

  protected HintFormula(Atom kind) {
    this.kind = kind;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    NockContext c = getContext(frame);
    Formula nextF = next();
    Hint        h = new Hint(kind, clue(frame), getSubject(frame), nextF.toNoun());
    Noun      pro = c.startHint(h);

    if ( null == pro ) {
      pro = nextF.executeNoun(frame);
      c.endHint(h, pro);
    }

    return pro;
  }
}
