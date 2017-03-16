package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;

@NodeChildren({
  @NodeChild(value="left", type=FormulaNode.class),
  @NodeChild(value="right", type=FormulaNode.class)
})
public abstract class BinaryFormulaNode extends FormulaNode {
  public abstract FormulaNode getLeft();
  public abstract FormulaNode getRight();
}
