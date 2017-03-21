package net.frodwith.jaque.truffle.nodes.formula;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RootNode;

import net.frodwith.jaque.KickLabel;
import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TailException;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.DispatchNode;
import net.frodwith.jaque.truffle.nodes.DispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;
import net.frodwith.jaque.truffle.nodes.JaqueRootNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNodeGen;
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
    guards = { "driver != null",
               "isFine(loc, nodes, core)" })
  protected Object doJet(VirtualFrame frame, Cell core,
      @Cached("getLocation(core)") Location loc,
      @Cached("makeLocationNodes(loc)") FragmentationNode[] nodes,
      @Cached("getDriver(loc, axis)") ImplementationNode driver) {
    return driver.doJet(core);
  }

  @TruffleBoundary
  protected FragmentationNode[] makeLocationNodes(Location loc) {
    CompilerAsserts.neverPartOfCompilation();
    if ( null == loc ) {
      return null;
    }
    LinkedList<FragmentationNode> list = new LinkedList<FragmentationNode>();
    while ( loc.parent != null ) {
      list.add(new FragmentationNode(loc.axisToParent));
      loc = loc.parent;
    }
    return list.toArray(new FragmentationNode[0]);
  }

  /* This is a specialization guard, so there is no point profiling it */
  @ExplodeLoop
  protected boolean isFine(Location loc, FragmentationNode[] nodes, Object noun) {
    CompilerAsserts.compilationConstant(nodes.length);
    if ( null == loc ) {
      return false;
    }
    for ( int i = 0; i < nodes.length; ++i ) {
      if ( !TypesGen.isCell(noun) ) {
        return false;
      }
      Cell currentCore = TypesGen.asCell(noun);
      if ( currentCore.head != loc.noun ) {
        return false;
      }
      noun = nodes[i].executeFragment(currentCore);
      loc  = loc.parent;
    }
    return loc.noun == noun;
  }
  
  @TruffleBoundary // hash operations
  protected Location getLocation(Cell core) {
    return getContext().locations.get(TypesGen.asCell(core.head));
  }

  @TruffleBoundary
  protected ImplementationNode getDriver(Location loc, Object axis) {
    CompilerAsserts.neverPartOfCompilation();
    if ( null == loc ) {
      return null;
    }
    Class<? extends ImplementationNode> klass = loc.drivers.get(axis);
    if ( null == klass ) {
      return null;
    }
    try {
      Method cons = klass.getMethod("create", Context.class);
      return (ImplementationNode) cons.invoke(null, getContext());
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

  // Unregistered location, cached target, tail call
  @Specialization(
    limit = "1",
    guards = { "getInBattery()",
               "getIsTail()",
               "core.head == cachedBattery" })
  protected Object doCachedTail(Cell core,
      @Cached("core.head") Object cachedBattery,
      @Cached("getLabel(core)") KickLabel label,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getTarget(label, core, fragment)") CallTarget target) {
    throw new TailException(target, new Object[] { core });
  }
  
  protected FragmentationNode getFragmentationNode() {
    return new FragmentationNode(getAxis());
  }
  
  protected KickLabel getLabel(Cell core) {
    Cell battery = TypesGen.asCell(core.head);
    return new KickLabel(battery, getAxis());
  }

  @TruffleBoundary // hash operations
  protected CallTarget getTarget(KickLabel label, Cell core, FragmentationNode fragment) {
    CompilerAsserts.neverPartOfCompilation();
    Context context = getContext();
    Map<KickLabel,CallTarget> kicks = context.kicks;
    CallTarget t = kicks.get(label);
    if ( null == t ) {
      Cell formula = TypesGen.asCell(fragment.executeFragment(core));
      FormulaNode f = context.parseCell(formula, true);
      RootNode root = new JaqueRootNode(f);
      t = Truffle.getRuntime().createCallTarget(root);
      kicks.put(label, t);
    }
    return t;
  }

  // Unregistered location, cached target, direct call
  @Specialization(
    limit = "1",
    guards = { "getInBattery()",
               "core.head == cachedBattery" })
  protected Object doCachedCall(VirtualFrame frame, Cell core,
      @Cached("core.head") Object cachedBattery,
      @Cached("getLabel(core)") KickLabel label,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getTarget(label, core, fragment)") CallTarget target,
      @Cached("getDispatch()") DispatchNode dispatch) {
    return dispatch.call(frame, target, new Object[] { core });
  }

  protected DispatchNode getDispatch() {
    return DispatchNodeGen.create();
  }
  
  // Unregistered location, varying target, tail
  @Specialization(
    guards = { "getInBattery()",
               "getIsTail()" })
  protected Object doSlowTail(Cell core,
      @Cached("getFragmentationNode()") FragmentationNode fragment) {
    KickLabel label = getLabel(core);
    throw new TailException(getTarget(label, core, fragment), new Object[] { core });
  }

  // Unregistered location, varying target, direct call
  @Specialization(
    guards = { "getInBattery()" })
  protected Object doSlowCall(VirtualFrame frame, Cell core,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getDispatch()") DispatchNode dispatch) {
    KickLabel label = getLabel(core);
    return dispatch.call(frame, getTarget(label, core, fragment), new Object[] { core });
  }
  
  // kicked arm isn't even in the battery - treat as nock
  @Specialization
  protected Object doNock(VirtualFrame frame, Cell core,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getNockDispatch()") NockDispatchNode dispatch) {
    Cell formula = TypesGen.asCell(fragment.executeFragment(core));
    return dispatch.executeNock(frame, core, formula);
  }
  
  protected NockDispatchNode getNockDispatch() {
    return NockDispatchNodeGen.create(getContext(), getIsTail());
  }
  
}
