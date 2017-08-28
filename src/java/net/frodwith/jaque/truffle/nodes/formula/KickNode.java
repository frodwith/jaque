package net.frodwith.jaque.truffle.nodes.formula;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Supplier;

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
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.KickLabel;
import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.FragmentationException;
import net.frodwith.jaque.truffle.TailException;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.DispatchNode;
import net.frodwith.jaque.truffle.nodes.DispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;
import net.frodwith.jaque.truffle.nodes.JaqueRootNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

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
    guards = { "driver != null",
               "isFine(constants, nodes, core)" })
  protected Object doJet(VirtualFrame frame, Cell core,
      @Cached("getLocation(core)") Location loc,
      @Cached("makeConstants(loc)") Object[] constants,
      @Cached("makeLocationNodes(loc)") FragmentationNode[] nodes,
      @Cached("getLabel(core)") KickLabel label,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getTarget(label, core, fragment)") CallTarget target,
      @Cached("getDriver(loc, axis, target)") ImplementationNode driver) {
    if ( getContext().profile ) {
      return squall(core, () -> driver.doJet(frame, core));
    }
    else {
      return driver.doJet(frame, core);
    }
  }
  
  protected Object[] makeConstants(Location loc) {
    if ( null == loc) {
      return null;
    }
    if ( loc.isStatic ) {
      return new Object[] { loc.noun };
    }
    loc = loc.parent; // skip the first location, battery already checked
    LinkedList<Object> list = new LinkedList<Object>();
    while ( !loc.isStatic ) {
      list.add(loc.noun);
      loc = loc.parent;
    }
    list.add(loc.noun);
    return list.toArray(new Object[0]);
  }

  @TruffleBoundary
  protected FragmentationNode[] makeLocationNodes(Location loc) {
    CompilerAsserts.neverPartOfCompilation();
    if ( null == loc ) {
      return null;
    }
    LinkedList<FragmentationNode> list = new LinkedList<FragmentationNode>();
    while ( !loc.isStatic ) {
      list.add(new FragmentationNode(loc.axisToParent));
      loc = loc.parent;
    }
    return list.toArray(new FragmentationNode[0]);
  }

  @ExplodeLoop
  protected boolean isFine(Object[] constants, FragmentationNode[] nodes, Object noun) {
    try {
      int i, top = nodes.length - 1;
      for ( i = 0; i < top; ++i ) {
        noun = nodes[i].executeFragment(noun);
        if ( !batteryMatch(TypesGen.expectCell(constants[i]), TypesGen.expectCell(noun)) ) {
          return false;
        }
      }
      noun = nodes[i].executeFragment(noun);
      return constants[i].equals(noun);
    }
    catch ( FragmentationException e ) {
      return false;
    }
    catch ( UnexpectedResultException e ) {
      return false;
    }
  }
  
  @TruffleBoundary // hash operations
  protected Location getLocation(Cell core) {
    try {
      return getContext().locations.get(TypesGen.expectCell(core.head));
    }
    catch ( UnexpectedResultException e ) {
      return null;
    }
  }
  
  private static Set<String> noSeen = new HashSet<String>();

  @TruffleBoundary
  protected ImplementationNode getDriver(Location loc, Object axis, CallTarget fallback) {
    CompilerAsserts.neverPartOfCompilation();
    if ( null == loc ) {
      return null;
    }
    Context context = getContext();
    Class<? extends ImplementationNode> klass = context.getDriver(loc, axis);
    if ( null == klass ) {
      /*
      if ( axis.equals(2L) ) {
        if ( !noSeen.contains(loc.label) ) {
          noSeen.add(loc.label);
          getContext().err("Unjetted gate: " + loc.label);
        }

      }
      */
      return null;
    }
    try {
      Method cons = klass.getMethod("create", Context.class, CallTarget.class);
      return (ImplementationNode) cons.invoke(null, context, fallback);
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
  
  public Supplier<String> squawk(Cell core) {
    Location loc = getLocation(core);
    Object axis = getAxis();
    String name = loc == null ? "unregistered" : loc.label;
    if ( null == loc ) {
      return null;
    }
    String arm = loc.axisToName.containsKey(axis) 
               ? loc.axisToName.get(axis)
               : "/" + axis;
    Context c = getContext();
    String id = String.format("%s:%s", name, arm);
    c.come(id);
    return () -> c.flee(); 
 }
  
  public Object squall(Cell core, Supplier<Object> doIt) {
    Supplier<String> flee = squawk(core);
    Object r = doIt.get();
    if ( null != flee ) {
      flee.get();
    }
    return r;
  }

  // Unregistered location, cached target, tail call
  @Specialization(
    guards = { "getInBattery()",
               "getIsTail()",
               "batteryMatch(cachedBattery, core)" })
  protected Object doCachedTail(Cell core,
      @Cached("batteryCache(core)") Cell cachedBattery,
      @Cached("getLabel(core)") KickLabel label,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getTarget(label, core, fragment)") CallTarget target) {
    if ( getContext().profile ) {
      throw new TailException(target, new Object[] { core }, squawk(core));
    }
    else {
      throw new TailException(target, new Object[] { core });
    }
  }

  protected static Cell batteryCache(Cell core) {
    try {
      Cell battery = TypesGen.expectCell(core.head);
      battery.calculateMug();
      return battery;
    }
    catch (UnexpectedResultException e) {
      return null;
    }
  }

  protected static boolean batteryMatch(Cell cachedBattery, Cell core) {
    try {
      Cell testBattery = TypesGen.expectCell(core.head);
      return Cell.equals(testBattery, cachedBattery);
    }
    catch (UnexpectedResultException e) {
      return false;
    }
  }
  
  protected FragmentationNode getFragmentationNode() {
    return new FragmentationNode(getAxis());
  }
  
  protected KickLabel getLabel(Cell core) {
    try {
      Cell battery = TypesGen.expectCell(core.head);
      return new KickLabel(battery, getAxis());
    }
    catch (UnexpectedResultException e ) {
      return null;
    }
  }

  @TruffleBoundary // hash operations
  protected CallTarget getTarget(KickLabel label, Cell core, FragmentationNode fragment) {
    CompilerAsserts.neverPartOfCompilation();
    Context context = getContext();
    Map<KickLabel,CallTarget> kicks = context.kicks;
    CallTarget t = kicks.get(label);
    if ( null == t ) {
      Cell formula;
      try {
        formula = TypesGen.expectCell(fragment.executeFragment(core));
      }
      catch ( UnexpectedResultException e ) {
        throw new Bail();
      }
      catch ( FragmentationException e ) {
        throw new Bail();
      }
      
      Location reg = context.locations.get(core.head);
      FormulaNode f = context.parseCell(formula, true);
      RootNode root = (null == reg) ? new JaqueRootNode(f) : new JaqueRootNode(f, reg.label, getAxis());
      t = Truffle.getRuntime().createCallTarget(root);
      kicks.put(label, t);
    }
    return t;
  }

  // Unregistered location, cached target, direct call
  @Specialization(
    guards = { "getInBattery()",
               "batteryMatch(cachedBattery, core)" })
  protected Object doCachedCall(VirtualFrame frame, Cell core,
      @Cached("batteryCache(core)") Cell cachedBattery,
      @Cached("getLabel(core)") KickLabel label,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getTarget(label, core, fragment)") CallTarget target,
      @Cached("getDispatch()") DispatchNode dispatch) {
    if ( getContext().profile ) {
      return squall(core, () -> dispatch.call(frame,  target, new Object[] { core }));
    }
    else {
      return dispatch.call(frame, target, new Object[] { core });
    }
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
    CallTarget target = getTarget(label, core, fragment);
    if ( getContext().profile ) {
      throw new TailException(target, new Object[] { core }, squawk(core));
    }
    else {
      throw new TailException(target, new Object[] { core });
    }
  }

  // Unregistered location, varying target, direct call
  @Specialization(
    guards = { "getInBattery()" })
  protected Object doSlowCall(VirtualFrame frame, Cell core,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getDispatch()") DispatchNode dispatch) {
    KickLabel label = getLabel(core);
    if ( getContext().profile ) {
      return squall(core, () -> dispatch.call(frame, getTarget(label, core, fragment), new Object[] { core }));
    }
    return dispatch.call(frame, getTarget(label, core, fragment), new Object[] { core });
  }
  
  // kicked arm isn't even in the battery - treat as nock
  @Specialization
  protected Object doNock(VirtualFrame frame, Cell core,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getNockDispatch()") NockDispatchNode dispatch) {
    Cell formula;
    try {
      formula = TypesGen.expectCell(fragment.executeFragment(core));
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
    catch ( FragmentationException e ) {
      throw new Bail();
    }
    if ( getContext().profile ) {
      return squall(core, () -> dispatch.executeNock(frame, core, formula));
    }
    else {
      return dispatch.executeNock(frame, core, formula);
    }
  }
  
  protected NockDispatchNode getNockDispatch() {
    return NockDispatchNodeGen.create(getContext(), getIsTail());
  }
  
}
