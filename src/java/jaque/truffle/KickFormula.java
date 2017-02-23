package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "kick")
public final class KickFormula extends Formula {
  private final Atom axis;
  @Child private Formula core;
  @Child private KickDispatchNode dispatch;

  public KickFormula(Atom axis, Formula core) {
    this.axis     = axis;
    this.core     = core;
    this.dispatch = KickDispatchNodeGen.create();
  }

  @Override
  public Object execute(VirtualFrame frame) {
    Cell c;
    try {
      c = core.executeCell(frame);
    }
    catch (UnexpectedResultException e){
      throw new Bail();
    }
    return dispatch.executeKick(frame, c, axis);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(9), new Cell(axis, core.toNoun()));
  }
}
