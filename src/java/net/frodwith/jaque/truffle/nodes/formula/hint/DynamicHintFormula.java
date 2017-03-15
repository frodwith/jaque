package net.frodwith.jaque.truffle.nodes.formula.hint;

import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;
import net.frodwith.jaque.truffle.nodes.formula.JumpFormula;

public abstract class DynamicHintFormula extends JumpFormula {
  @Child protected FormulaNode hint;
  @Child protected FormulaNode next;
  
  protected DynamicHintFormula(FormulaNode hint, FormulaNode next) {
    this.hint = hint;
    this.next = next;
  }
}
