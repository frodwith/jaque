package jaque.truffle;

import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;

import jaque.noun.Atom;
import jaque.noun.Cell;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "comp")
public final class ComposeFormula extends UnsafeFormula {
  @Child private Formula f;
  @Child private Formula g;

  public ComposeFormula(Formula f, Formula g) {
    this.f = f;
    this.g = g;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    NockLanguage.setSubject(frame, f.executeSafe(frame));
    return g.execute(frame);
  }

  public Cell toCell() {
    return new Cell(7L, new Cell(f.toCell(), g.toCell()));
  }
}
