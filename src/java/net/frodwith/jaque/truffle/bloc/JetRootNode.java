package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.jet.ImplementationNode;

public class JetRootNode extends RootNode {
  private ImplementationNode impl;
  private String name;
  
  public JetRootNode(ImplementationNode impl, String name) {
    this.impl = impl;
    this.name = name;
    insert(impl);
  }
  
  @Override
  public String getName() {
    return name;
  }

  public Continuation execute(VirtualFrame frame) {
    @SuppressWarnings("unchecked")
    Stack<Object> s = (Stack<Object>) frame.getArguments()[0];
    //System.err.println("jet " + name);
    frame.setObject(BlocNode.STACK, s);
    s.push(impl.doJet(frame, s.pop()));
    // Jets never participate in the continuation protocol, even if they take
    // gates as arguments or use their fallback or something. They could, but it
    // would involve rewriting a lot of jets for no real benefit and might require
    // writing those jets in CPS (gross).
    return Continuation.ret();
  }
}
