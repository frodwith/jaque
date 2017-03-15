package net.frodwith.jaque.truffle.nodes.formula.hint;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

/* All dynamic hints must (per nock semantics) be computed and "discarded".
 * Other hint nodes (like FastHintNode) may do something with those hints,
 * but the purpose of this node is to literally discard the computed values.
 */
public class DiscardHintNode extends DynamicHintFormula {
  
  public DiscardHintNode(FormulaNode hint, FormulaNode next) {
    super(hint, next);
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    hint.executeGeneric(frame);
    return next.executeGeneric(frame);
  }
}
