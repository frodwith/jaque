package net.frodwith.jaque.truffle.jet;


import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.Continuation;

public abstract class ImplementationNode extends BlocNode {
  public abstract Continuation executeJet(VirtualFrame frame);
  
  // FIXME: the way this is set up, specialized return types aren't used (see warning
  //        about executeCell_ in DvrNodeGen
}
  