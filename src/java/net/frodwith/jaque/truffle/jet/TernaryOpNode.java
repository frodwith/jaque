package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.bloc.BlocNode;

public abstract class TernaryOpNode extends BlocNode {
  public abstract Object executeTernary(VirtualFrame frame, Object a, Object b, Object c);
}
