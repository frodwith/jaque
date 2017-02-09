package jaque.truffle;

import jaque.interpreter.*;
import jaque.noun.*;

@NodeInfo(shortName = "kick")
public final class KickNode extends Formula {
  private final Atom axis;
  @Child private Formula coreF;
  @Child private KickDispatchNode dispatch;

  public KickNode(Atom axis, Formula coreF) {
    this.axis        = axis;
    this.coreF       = core;
    this.dispatch    = KickDispatchNodeGen.create();
  }

  public Noun execute(VirtualFrame frame) {
    return dispatch.executeKick(coreF.executeCell(frame), axis);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(9), new Cell(axis, coreF.toNoun()));
  }
}
