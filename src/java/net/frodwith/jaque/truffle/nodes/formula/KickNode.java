package net.frodwith.jaque.truffle.nodes.formula;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Fragmenter;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.location.Location;
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
  @NodeField(name="fragmenter", type=Fragmenter.class),
})
public abstract class KickNode extends FormulaNode {
  protected abstract Context getContext();
  protected abstract boolean getIsTail();
  protected abstract Fragmenter getFragmenter();
  protected abstract boolean getInBattery();
  
  @Specialization(
    limit  = "1",
    guards = { "getInBattery()",
               "core.head == cachedBattery",
               "!(jetNode == null)",
               "isFine(location, core)" })
  protected Object doJet(VirtualFrame frame, Cell core,
    @Cached("core.head") Object cachedBattery,
    @Cached("getLocation(core)") Location location,
    @Cached("makeJetNode(getDriver(location))") JetNode jetNode) {
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
    @Cached("getDispatch()") DispatchNode dispatch) {
    return dispatch.call(frame, target, core);
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
    @Cached("getDispatch()") DispatchNode dispatch) {
    return dispatch.call(frame, getTarget(core), core);
  }

  @Specialization(
//    replaces = { "doSlowTail", "doCachedTail", "doJet" },
    guards = { "getIsTail()" })
  protected Object doNockTail(VirtualFrame frame, Cell core) {
    throw new TailException(getNockTarget(core), core);
  }
  
  // TODO: factor out the PIC logic for cells (from NockNode) into a dispatch
  //       node and call that from here. Low priority though, as this is ONLY
  //       called on kicks outside the battery.
  @Specialization(
//    replaces = { "doSlowCall", "doCachedCall", "doJet" }
      )
  protected Object doNockCall(VirtualFrame frame, Cell core,
    @Cached("getDispatch()") DispatchNode dispatch) {
    return dispatch.call(frame, getNockTarget(core), core);
  }
  
  protected CallTarget getNockTarget(Cell core) {
    Cell formula = TypesGen.asCell(getFragmenter().fragment(core));
    return getContext().getNock(formula);
  }
  
  @TruffleBoundary
  protected JetNode makeJetNode(Class<? extends ImplementationNode> driver) {
    if ( null == driver ) {
      return null;
    }
    CompilerAsserts.neverPartOfCompilation();
    try {
      Method cons = driver.getMethod("create", Context.class);
      ImplementationNode impl = (ImplementationNode) cons.invoke(null, getContext());
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
  
  protected CallTarget getTarget(Cell core) {
    return getContext().getKick(core, getFragmenter());
  }
  
  protected DispatchNode getDispatch() {
    return DispatchNodeGen.create();
  }
  
  protected Location getLocation(Cell core) {
    return getContext().lookup(core);
  }
  
  protected boolean isFine(Location loc, Cell core) {
    return (null == loc) ? false : loc.matches(core);
  }
  
  protected Class<? extends ImplementationNode> getDriver(Location loc) {
    return (null == loc) ? null : loc.find(getFragmenter());
  }
}
