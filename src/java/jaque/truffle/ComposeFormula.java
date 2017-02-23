package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.Node.Child;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "comp")
public final class ComposeFormula extends Formula {
  @Child private Formula f;
  @Child private Formula g;

  public ComposeFormula(Formula f, Formula g) {
    this.f = f;
    this.g = g;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    frame.getArguments()[0] = f.execute(frame);
    return g.execute(frame);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(7), new Cell(f.toNoun(), g.toNoun()));
  }
}
