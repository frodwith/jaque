package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class ReadOpNode extends BlocNode {
  public abstract Object executeRead(VirtualFrame frame, Object from);
}
