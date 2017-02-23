package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;

@NodeInfo(shortName = "nock")
@NodeChildren({@NodeChild("subject"), @NodeChild("formula")})
public abstract class NockFormula extends Formula {
  
  protected abstract Formula getSubject();
  protected abstract Formula getFormula();

  @Specialization(guards = {"formula.equals(cachedFormula)"})
  public Object doDirect(VirtualFrame frame, Noun subject, Cell formula,
    @Cached("formula") Cell cachedFormula,
    @Cached("create(getTarget(formula))") DirectCallNode callNode) 
  {
    return callNode.call(frame, new Object[] {getContext(frame), subject});
  }

  @Specialization
  public Object doIndirect(VirtualFrame frame, Noun subject, Cell formula,
    @Cached("create()") IndirectCallNode callNode) 
  {
    CallTarget target = getTarget(formula);
    return callNode.call(frame, target, new Object[] {getContext(frame), subject});
  }
  
  protected static CallTarget getTarget(Cell formula) {
    return Truffle.getRuntime().createCallTarget(new NockRootNode(Formula.fromCell(formula)));
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(2), new Cell(getSubject().toNoun(), getFormula().toNoun()));
  }
}
