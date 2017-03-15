package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.nodes.FunctionNode;
import net.frodwith.jaque.truffle.nodes.JaqueNode;

public class JetNode extends FunctionNode {
  @Child private ImplementationNode impl;
  
  public JetNode(ImplementationNode impl) {
    this.impl = impl;
  }
  
  public Object executeGeneric(VirtualFrame frame) {
    return impl.doJet(getSubject(frame));
  }
}
