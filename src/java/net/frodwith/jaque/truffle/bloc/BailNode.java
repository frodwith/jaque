package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.Bail;

public final class BailNode extends OpNode {

  @Override
  public void execute(VirtualFrame frame) {
    throw new Bail();
  }

}
