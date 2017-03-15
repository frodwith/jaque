package net.frodwith.jaque.truffle.nodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TailException;
import net.frodwith.jaque.truffle.nodes.DispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;
import net.frodwith.jaque.truffle.nodes.jet.JetNode;

@NodeFields({
  @NodeField(name="context", type=Context.class),
  @NodeField(name="isTail", type=Boolean.class)
})
public abstract class KickDispatchNode extends JaqueNode {
  public abstract Object executeKick(VirtualFrame frame, Cell core, Object axis);
  protected abstract Context getContext();
  protected abstract boolean getIsTail();
  
  @Specialization(
    limit  = "1",
    guards = { "!(jetNode == null)", 
               "matchBattery(core, cachedBattery, axis)",
               "fine(core)" })
  protected Object doJet(VirtualFrame frame, Cell core, Object axis,
    @Cached("core.head") Object cachedBattery,
    @Cached("find(core, axis)") JetNode jetNode) {
    return jetNode.executeGeneric(frame);
  }

  @Specialization(
    replaces = "doJet",
    limit = "1",
    guards = { "getIsTail()",
               "matchBattery(core, cachedBattery, axis)" })
  protected Object doCachedTail(Cell core, Object axis,
    @Cached("core.head") Object cachedBattery,
    @Cached("getTarget(core, axis)") CallTarget target) {
    throw new TailException(target, core);
  }
  
  @Specialization(
    replaces = "doJet",
    limit = "1",
    guards = { "matchBattery(core, cachedBattery, axis)" })
  protected Object doCachedCall(VirtualFrame frame, Cell core, Object axis,
    @Cached("core.head") Object cachedBattery,
    @Cached("getTarget(core, axis)") CallTarget target,
    @Cached("makeDispatch()") DispatchNode dispatch) {
    return dispatch.executeDispatch(frame, target, core);
  }

  @Specialization(
    replaces = { "doCachedTail", "doJet" }, 
    guards = { "getIsTail()" })
  protected Object doSlowTail(Cell core, Atom axis) {
    throw new TailException(getTarget(core, axis), core);
  }

  @Specialization(
    replaces = { "doCachedCall", "doJet" })
  protected Object doSlowCall(VirtualFrame frame, Cell core, Atom axis,
      @Cached("makeDispatch()") DispatchNode dispatch) {
    return dispatch.executeDispatch(frame, getTarget(core, axis), core);
  }
  
  protected DispatchNode makeDispatch() {
    return DispatchNodeGen.create();
  }
  
  protected boolean matchBattery(Cell core, Object battery, Object axis) {
    return (Atom.cap(axis) == 2) && (core.head == battery);
  }
  
  protected boolean fine(Cell core) {
    return getContext().fine(core);
  }
  
  protected JetNode find(Cell core, Object axis) {
    Context context = getContext();
    Class<? extends ImplementationNode> driver = context.find(core, axis);
    if ( null == driver ) {
      return null;
    }
    else {
      try {
        CompilerDirectives.transferToInterpreter();
        Constructor<? extends ImplementationNode> cons = driver.getConstructor(Context.class);
        ImplementationNode impl = cons.newInstance(context);
        return new JetNode(impl);
      }
      catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
      catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      catch (InstantiationException e) {
        e.printStackTrace();
      }
      catch (InvocationTargetException e) {
        e.printStackTrace();
      }
      return null;
    }
  }
  
  protected CallTarget getTarget(Cell core, Object axis) {
    return getContext().getKick(core, axis);
  }
}