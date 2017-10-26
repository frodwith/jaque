package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class OpNode extends BlocNode {
  public abstract void execute(VirtualFrame frame);
}
