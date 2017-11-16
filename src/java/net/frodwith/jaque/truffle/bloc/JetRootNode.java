package net.frodwith.jaque.truffle.bloc;

import java.util.Deque;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.jet.ImplementationNode;

public class JetRootNode extends RootNode {
  private final String name;
  @Child private ImplementationNode impl;
  
  public JetRootNode(String name, ImplementationNode impl) {
    this.name = name;
    this.impl = impl;
  }
  
  @Override
  public String getName() {
    return name;
  }

  public Continuation execute(VirtualFrame frame) {
    @SuppressWarnings("unchecked")
    Deque<Object> s = (Deque<Object>) frame.getArguments()[0];
    frame.setObject(BlocNode.STACK, s);
    return impl.executeJet(frame);
  }
}
