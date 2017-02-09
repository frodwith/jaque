package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "comp")
public final class ComposeNode extends Formula {
  @Child private Formula f;
  @Child private Formula g;

  @Override
  public Object execute(VirtualFrame frame) {
    frame.getArguments()[0] = f.execute(frame);
    return g.execute(frame);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(7), new Cell(f.toNoun(), g.toNoun()));
  }
}
