package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.bloc.BlocNode;

public abstract class UnaryOpNode extends BlocNode {
  public abstract Object executeUnary(VirtualFrame frame, Object a);
}
