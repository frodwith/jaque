package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Cached;

@TypeSystemReference(NockTypes.class)
public abstract class NockDispatchNode extends Node {

  public abstract Object executeNock(VirtualFrame frame, Noun subject, Cell formula);

  @Specialization(guards = {"formula.equals(cachedFormula)"})
  protected static Noun doStatic(VirtualFrame frame, Noun subject, Cell formula,
    @Cached("formula") Cell cachedFormula,
    @Cached("create(targetFromCell(frame, cachedFormula))") DirectCallNode callNode)
  {
    Object[] args = { subject, frame.getArguments()[1] };
    callNode.call(frame, args);
  }

  @Specialization(replaces = {"doStatic"})
  protected static Noun doDynamic(VirtualFrame frame, Noun subject, Formula formulaF,
     @Cached("create()") IndirectCallNode callNode)
  {
    Object[] args = { subject, frame.getArguments()[1] };
    callNode.call(frame, targetFromCell(frame, formula), args);
  }

  protected static CallTarget targetFromCell(VirtualFrame frame, Cell cell) {
    Map<Cell,CallTarget> map = (Map<Cell,CallTarget>) (frame.getArguments()[1]);
    CallTarget        target = map.get(cell);

    if ( null == target ) {
      target = Truffle.getRuntime().createCallTarget(Formula.fromNoun(cell));
      map.put(cell, target);
    }

    return target;
  }
}
