package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "push")
public final class PushFormula extends Formula {
  @Child private Formula f;
  @Child private Formula g;
  private final DirectCallNode callNode;

  public PushFormula(Formula f, Formula g) {
    this.f = f;
    this.g = g;
    this.callNode = DirectCallNode.create(Truffle.getRuntime().createCallTarget(new NockRootNode(g)));
  }

  @Override
  public Object execute(VirtualFrame frame) {
    Cell subject = new Cell(f.execute(frame), getSubject(frame));
    return callNode.call(frame, new Object[] { getContext(frame), subject });
  }

  public Cell toCell() {
    return new Cell(8, new Cell(f.toCell(), g.toCell()));
  }
}
