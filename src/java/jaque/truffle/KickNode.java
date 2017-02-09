package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeInfo(shortName = "kick")
public final class KickNode extends Formula {
  private final Atom axis;
  @Child private Formula core;
  @Child private KickDispatchNode dispatch;

  public KickNode(Atom axis, Formula core) {
    this.axis     = axis;
    this.core     = core;
    this.dispatch = KickDispatchNodeGen.create();
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return dispatch.executeKick(core.executeCell(frame), axis);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(9), new Cell(axis, core.toNoun()));
  }
}
