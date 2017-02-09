package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

@NodeInfo(shortName = "push")
public final class PushNode extends Formula {
  @Child private Formula f;
  @Child private Formula g;

  public Noun execute(VirtualFrame frame) {
    Noun head  = f.executeNoun(frame);
    Object[] a = frame.getArguments();
    a[0] = new Cell(head, a[0]);
    return g.executeNoun(frame);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(8), new Cell(f.toNoun(), g.toNoun()));
  }
}
