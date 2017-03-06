package jaque.truffle;

import jaque.interpreter.Bail;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.util.Arrays;

import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "frag")
public final class FragFormula extends SafeFormula {
  private final Atom axis;
  private final Fragmenter f;

  public FragFormula(Atom axis) {
    this.axis = axis;
    this.f    = new Fragmenter(axis);
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    return f.fragment(getSubject(frame));
  }

  public Cell toCell() {
    return new Cell(0L, axis);
  }
}
