package net.frodwith.jaque.truffle.nodes.formula.hint;

import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

public abstract class DynamicHintFormula extends FormulaNode {
  @Child protected FormulaNode hint;
  @Child protected FormulaNode next;
  
  protected DynamicHintFormula(FormulaNode hint, FormulaNode next) {
    this.hint = hint;
    this.next = next;
  }
}
