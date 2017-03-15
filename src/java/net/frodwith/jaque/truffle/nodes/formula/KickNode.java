package net.frodwith.jaque.truffle.nodes.formula;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TailException;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.DispatchNode;
import net.frodwith.jaque.truffle.nodes.DispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;
import net.frodwith.jaque.truffle.nodes.jet.JetNode;

@NodeChild(value="core", type=FormulaNode.class)
@NodeFields({
  @NodeField(name="context", type=Context.class),
  @NodeField(name="isTail", type=Boolean.class),
  @NodeField(name="inBattery", type=Boolean.class),
  @NodeField(name="axis", type=Object.class),
})
public abstract class KickNode extends FormulaNode {
  protected abstract Context getContext();
  protected abstract boolean getIsTail();
  protected abstract Object getAxis();
  protected abstract boolean getInBattery();
  
  @Specialization(
    limit  = "1",
    guards = { "getInBattery()",
               "!(jetNode == null)", 
               "core.head == cachedBattery",
               "fine(core)" })
  protected Object doJet(VirtualFrame frame, Cell core,
    @Cached("core.head") Object cachedBattery,
    @Cached("find(core)") JetNode jetNode) {
    setSubject(frame, core);
    return jetNode.executeGeneric(frame);
  }

  @Specialization(
    limit = "1",
//    replaces = "doJet",
    guards = { "getInBattery()",
               "getIsTail()",
               "core.head == cachedBattery" })
  protected Object doCachedTail(Cell core,
    @Cached("core.head") Object cachedBattery,
    @Cached("getTarget(core)") CallTarget target) {
    throw new TailException(target, core);
  }

  @Specialization(
    limit = "1",
//    replaces = "doJet",
    guards = { "getInBattery()",
               "core.head == cachedBattery" })
  protected Object doCachedCall(VirtualFrame frame, Cell core,
    @Cached("core.head") Object cachedBattery,
    @Cached("getTarget(core)") CallTarget target,
    @Cached("makeDispatch()") DispatchNode dispatch) {
    return dispatch.executeDispatch(frame, target, core);
  }
  
  @Specialization(
//    replaces = { "doCachedTail", "doJet" },
    guards = { "getInBattery()",
               "getIsTail()" })
  protected Object doSlowTail(Cell core) {
    throw new TailException(getTarget(core), core);
  }

  @Specialization(
//    replaces = { "doCachedCall", "doJet" },
    guards = { "getInBattery()" })
  protected Object doSlowCall(VirtualFrame frame, Cell core,
      @Cached("makeDispatch()") DispatchNode dispatch) {
    return dispatch.executeDispatch(frame, getTarget(core), core);
  }
  @Specialization(
//    replaces = { "doSlowTail", "doCachedTail", "doJet" },
    guards = { "getIsTail()" })
  protected Object doNockTail(VirtualFrame frame, Cell core) {
    throw new TailException(getNockTarget(core), core);
  }
  
  @Specialization(
//    replaces = { "doSlowCall", "doCachedCall", "doJet" }
      )
  protected Object doNockCall(VirtualFrame frame, Cell core,
    @Cached("makeDispatch()") DispatchNode dispatch) {
    return dispatch.executeDispatch(frame, getNockTarget(core), core);
  }
  
  protected CallTarget getNockTarget(Cell core) {
    Cell formula = TypesGen.asCell(Noun.fragment(getAxis(), core));
    return getContext().getNock(formula);
  }

  protected DispatchNode makeDispatch() {
    return DispatchNodeGen.create();
  }
  
  protected boolean fine(Cell core) {
    boolean wasFine = getContext().fine(core);
    return wasFine;
  }
  
  protected JetNode find(Cell core) {
    Context context = getContext();
    Class<? extends ImplementationNode> driver = context.find(core, getAxis());
    if ( null == driver ) {
      return null;
    }
    else {
      try {
        CompilerAsserts.neverPartOfCompilation();
        Method cons = driver.getMethod("create", Context.class);
        ImplementationNode impl = (ImplementationNode) cons.invoke(null, context);
        return new JetNode(impl);
      }
      catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
      catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      catch (InvocationTargetException e) {
        e.printStackTrace();
      }
      return null;
    }
  }
  
  protected CallTarget getTarget(Cell core) {
    return getContext().getKick(core, getAxis());
  }
}
