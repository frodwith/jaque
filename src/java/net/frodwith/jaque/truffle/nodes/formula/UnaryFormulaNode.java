package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.NodeChild;

@NodeChild(value="sub", type=FormulaNode.class)
public abstract class UnaryFormulaNode extends FormulaNode {
  public abstract FormulaNode getSub();
}
