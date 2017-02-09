package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

@NodeInfo(shortName = "comp")
public final class ComposeNode extends Formula {
  @Child private Formula f;
  @Child private Formula g;

  public Noun execute(VirtualFrame frame) {
    frame.getArguments()[0] = f.executeNoun(frame);
    return g.executeNoun(frame);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(7), new Cell(f.toNoun(), g.toNoun()));
  }
}
