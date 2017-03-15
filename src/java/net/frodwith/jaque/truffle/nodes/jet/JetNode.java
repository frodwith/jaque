package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.nodes.FunctionNode;

public class JetNode extends FunctionNode {
  @Child private ImplementationNode impl;
  
  public JetNode(ImplementationNode impl) {
    this.impl = impl;
  }
  
  public Object executeGeneric(VirtualFrame frame) {
    return impl.doJet(getSubject(frame));
  }
}
