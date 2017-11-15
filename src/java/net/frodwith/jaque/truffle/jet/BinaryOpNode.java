package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.bloc.BlocNode;

public abstract class BinaryOpNode extends BlocNode {
  public abstract Object executeBinary(VirtualFrame frame, Object a, Object b);
}
