package net.frodwith.jaque.truffle.nodes.formula.hint;

import net.frodwith.jaque.truffle.nodes.formula.Formula;
import net.frodwith.jaque.truffle.nodes.formula.JumpFormula;

public abstract class DynamicHintFormula extends JumpFormula {
  @Child protected Formula hint;
  @Child protected Formula next;
  
  protected DynamicHintFormula(Formula hint, Formula next) {
    this.hint = hint;
    this.next = next;
  }
}
