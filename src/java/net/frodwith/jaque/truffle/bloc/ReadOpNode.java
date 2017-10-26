package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.nodes.JaqueNode;

public abstract class ReadOpNode extends JaqueNode {
  public abstract Object executeRead(VirtualFrame frame, Object from);
}
