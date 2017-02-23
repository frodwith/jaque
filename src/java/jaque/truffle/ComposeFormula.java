package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "comp")
public final class ComposeFormula extends Formula {
  @Child private Formula f;
  @Child private Formula g;
  private final DirectCallNode callNode;

  public ComposeFormula(Formula f, Formula g) {
    this.f = f;
    this.g = g;
    this.callNode = DirectCallNode.create(Truffle.getRuntime().createCallTarget(new NockRootNode(g)));
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return callNode.call(frame, new Object[] { getContext(frame), f.execute(frame) });
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(7), new Cell(f.toNoun(), g.toNoun()));
  }
}
