package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.NodeField;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.nodes.JaqueNode;

@NodeField(name="context", type=Context.class)
public abstract class JetNode extends JaqueNode {
  public abstract Context getContext();
  public abstract Object doJet(Cell core);
}
