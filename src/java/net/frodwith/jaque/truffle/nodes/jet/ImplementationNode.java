package net.frodwith.jaque.truffle.nodes.jet;


import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.nodes.JaqueNode;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

@NodeFields({
  @NodeField(name="context", type=Context.class),
  @NodeField(name="fallback", type=FormulaNode.class)
})
public abstract class ImplementationNode extends JaqueNode {
  public abstract Object doJet(VirtualFrame frame, Object subject);
  public abstract Context getContext();
  public abstract FormulaNode getFallback();
}