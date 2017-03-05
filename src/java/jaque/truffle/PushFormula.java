package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "push")
public final class PushFormula extends UnsafeFormula {
  @Child private Formula f;
  @Child private Formula g;

  public PushFormula(Formula f, Formula g) {
    this.f = f;
    this.g = g;
  }
  

  @Override
  public Object execute(VirtualFrame frame) {
    Object tail = getSubject(frame);
    Object head = f.executeSafe(frame);
    NockLanguage.setSubject(frame, new Cell(head, tail));
    return g.execute(frame);
  }

  public Cell toCell() {
    return new Cell(8L, new Cell(f.toCell(), g.toCell()));
  }
}
