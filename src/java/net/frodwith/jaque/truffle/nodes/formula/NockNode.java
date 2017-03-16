package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TailException;
import net.frodwith.jaque.truffle.nodes.DispatchNode;
import net.frodwith.jaque.truffle.nodes.DispatchNodeGen;

@NodeFields({
  @NodeField(name="context", type=Context.class),
  @NodeField(name="isTail", type=Boolean.class)
})
public abstract class NockNode extends BinaryFormulaNode {
  public abstract Context getContext();
  public abstract boolean getIsTail();
  private static final String CACHE_SIZE = "3";
  
  @Specialization(
    limit = CACHE_SIZE,
    guards = { "getIsTail()",
               "formula == cachedFormula" })
  public Object doTailCached(Object subject, Cell formula,
    @Cached("formula") Cell cachedFormula,
    @Cached("getTarget(formula)") CallTarget target) {
    throw new TailException(target, subject);
  }

  @Specialization(
    limit = CACHE_SIZE,
    guards = "formula == cachedFormula")
  public Object doCachedCall(VirtualFrame frame, Object subject, Cell formula,
    @Cached("formula") Cell cachedFormula,
    @Cached("getTarget(formula)") CallTarget target,
    @Cached("getDispatch()") DispatchNode dispatch) {
    return dispatch.call(frame, target, subject);
  }

  @Specialization(
    replaces = "doTailCached",
      guards = { "getIsTail()" })
  public Object doTailSlow(Object subject, Cell formula) {
    throw new TailException(getTarget(formula), subject);
  }

  @Specialization(
    replaces = "doCachedCall")
  public Object doSlowCall(VirtualFrame frame, Object subject, Cell formula,
    @Cached("getDispatch()") DispatchNode dispatch) {
    return dispatch.call(frame, getTarget(formula), subject);
  }
  
  protected CallTarget getTarget(Cell formula) {
    return getContext().getNock(formula);
  }

  protected DispatchNode getDispatch() {
    return DispatchNodeGen.create();
  }
}
