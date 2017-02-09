package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "push")
public final class PushNode extends Formula {
  @Child private Formula f;
  @Child private Formula g;

  @Override
  public Object execute(VirtualFrame frame) {
    Noun head  = f.executeNoun(frame);
    Object[] a = frame.getArguments();
    a[0] = new Cell(head, (Noun) a[0]);
    return g.execute(frame);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(8), new Cell(f.toNoun(), g.toNoun()));
  }
}
