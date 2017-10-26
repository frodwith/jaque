package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.nodes.JaqueNode;

public abstract class FineOpNode extends JaqueNode {
  public abstract boolean executeFine(VirtualFrame frame, Object got);
}
