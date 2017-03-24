package net.frodwith.jaque.truffle.nodes;

import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TailException;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

@NodeFields({
  @NodeField(name="context", type=Context.class),
  @NodeField(name="isTail", type=Boolean.class)
})
public abstract class NockDispatchNode extends JaqueNode {
  private static final String CACHE_SIZE = "3";

  public abstract Object executeNock(VirtualFrame frame, Object subject, Cell formula);
  public abstract Context getContext();
  public abstract boolean getIsTail();
  
  @Specialization(
    limit = CACHE_SIZE,
    guards = { "getIsTail()",
               "formula == cachedFormula" })
  public Object doTailCached(Object subject, Cell formula,
    @Cached("formula") Cell cachedFormula,
    @Cached("getTarget(formula)") CallTarget target) {
    throw new TailException(target, new Object[] { subject });
  }

  @Specialization(
    limit = CACHE_SIZE,
    guards = "formula == cachedFormula")
  public Object doCachedCall(VirtualFrame frame, Object subject, Cell formula,
    @Cached("formula") Cell cachedFormula,
    @Cached("getTarget(formula)") CallTarget target,
    @Cached("getDispatch()") DispatchNode dispatch) {
    return dispatch.call(frame, target, new Object[] { subject });
  }

  @Specialization(
    replaces = "doTailCached",
      guards = { "getIsTail()" })
  public Object doTailSlow(Object subject, Cell formula) {
    throw new TailException(getTarget(formula), new Object[] { subject });
  }

  @Specialization(
    replaces = "doCachedCall")
  public Object doSlowCall(VirtualFrame frame, Object subject, Cell formula,
    @Cached("getDispatch()") DispatchNode dispatch) {
    return dispatch.call(frame, getTarget(formula), new Object[] { subject });
  }

  @TruffleBoundary // hash operations
  protected CallTarget getTarget(Cell formula) {
    CompilerAsserts.neverPartOfCompilation();
    Context context = getContext();
    Map<Cell,CallTarget> nocks = context.nocks;
    CallTarget t = nocks.get(formula);
    if ( null == t ) {
      FormulaNode f = context.parseCell(formula, true);
      RootNode root = new JaqueRootNode(f);
      t = Truffle.getRuntime().createCallTarget(root);
      nocks.put(formula, t);
    }
    return t;
  }
  
  protected DispatchNode getDispatch() {
    return DispatchNodeGen.create();
  }
}
