package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public class JetRootNode extends RootNode {
  private ImplementationNode impl;
  
  public JetRootNode(ImplementationNode impl) {
    this.impl = impl;
  }

  public Continuation execute(VirtualFrame frame) {
    @SuppressWarnings("unchecked")
    Stack<Object> s = (Stack<Object>) frame.getArguments()[0];
    frame.setObject(BlocNode.STACK, s);
    s.push(impl.doJet(frame, s.pop()));
    // Jets never participate in the continuation protocol, even if they take
    // gates as arguments or use their fallback or something. They could, but it
    // would involve rewriting a lot of jets for no real benefit and might require
    // writing those jets in CPS (gross).
    return null;
  }
}
