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
  private final boolean[] path;

  public FragFormula(Atom axis) {
    this.axis = axis;
    if ( Atom.ZERO.equals(axis) ) {
      this.path = null;
    }
    else {
      this.path = axis.fragments();
    }
  }

  @ExplodeLoop
  public Object execute(VirtualFrame frame) {
    if ( null == this.path ) {
      throw new Bail();
    }
    else {
      Object r = getSubject(frame);
      for ( boolean tail : path ) {
        if (r instanceof Cell) {
          Cell c = (Cell) r;
          r = tail ? c.getTail() : c.getHead();
        }
        else {
          throw new Bail();
        }
      }
      return r;
    }
  }

  public Cell toCell() {
    return new Cell(0L, axis);
  }
}
