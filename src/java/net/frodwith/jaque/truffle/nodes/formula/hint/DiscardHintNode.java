package net.frodwith.jaque.truffle.nodes.formula.hint;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.nodes.formula.Formula;
import net.frodwith.jaque.truffle.nodes.formula.JumpFormula;

/* All dynamic hints must (per nock semantics) be computed and "discarded".
 * Other hint nodes (like FastHintNode) may do something with those hints,
 * but the purpose of this node is to literally discard the computed values.
 */
public class DiscardHintNode extends DynamicHintFormula {
  
  public DiscardHintNode(Formula hint, Formula next) {
    super(hint, next);
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object subject = getSubject(frame);
    hint.executeSafe(frame);
    setSubject(frame, subject);
    return next.executeGeneric(frame);
  }
}
