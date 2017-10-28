package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class FineOpNode extends BlocNode {
  public abstract boolean executeFine(VirtualFrame frame, Object got);
}
