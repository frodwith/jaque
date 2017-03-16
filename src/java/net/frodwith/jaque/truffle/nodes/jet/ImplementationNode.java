package net.frodwith.jaque.truffle.nodes.jet;


import com.oracle.truffle.api.dsl.NodeField;

import net.frodwith.jaque.data.Fragmenter;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.nodes.JaqueNode;

@NodeField(name="context", type=Context.class)
public abstract class ImplementationNode extends JaqueNode {
  protected static final Fragmenter sampler = new Fragmenter(6L);

  public abstract Object doJet(Object subject);
  public abstract Context getContext();
}