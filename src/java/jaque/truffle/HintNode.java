package jaque.truffle;

import jaque.interpreter.Result;
import jaque.interpreter.Hint;
import jaque.noun.*;

public abstract class HintNode extends Formula {
  public abstract Result clue(Environment e);
  public abstract Cell rawNext();
  public abstract Atom kind();
  public abstract Formula next();

  public Result apply(Environment e) {
    Result  r = clue(e);
    Hint    h = new Hint(kind(), r.r, e.subject, rawNext());

    r = r.m.startHint(h);
    if ( null != r.r ) {
      return r;
    }
    else {
      r = next().apply(new Environment(r.m, e.subject));
      return new Result(r.m.endHint(h, r.r), r.r);
    }
  }
}
