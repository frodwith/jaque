package net.frodwith.jaque.truffle.jet;


import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;

@NodeFields({
  @NodeField(name="context", type=Context.class),
  @NodeField(name="fallback", type=DirectCallNode.class)
})
public abstract class ImplementationNode extends BlocNode {
  public abstract Object doJet(VirtualFrame frame, Object subject);
  public abstract Context getContext();
  public abstract DirectCallNode getFallback();
  
  // FIXME: the way this is set up, specialized return types aren't used (see warning
  //        about executeCell_ in DvrNodeGen
}