package net.frodwith.jaque.truffle.nodes.jet;


import com.oracle.truffle.api.dsl.NodeField;

import net.frodwith.jaque.data.Fragmenter;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.nodes.JaqueNode;

@NodeField(name="context", type=Context.class)
public abstract class ImplementationNode extends JaqueNode {
  public abstract Object doJet(Object subject);
  public abstract Context getContext();
}