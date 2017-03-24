package net.frodwith.jaque.truffle.nodes.formula;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
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
import com.oracle.truffle.api.object.DynamicObject;

import net.frodwith.jaque.KickLabel;
import net.frodwith.jaque.Registration;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Fragment;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TailException;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.DispatchNode;
import net.frodwith.jaque.truffle.nodes.DispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;
import net.frodwith.jaque.truffle.nodes.JaqueRootNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.ReadNode;
import net.frodwith.jaque.truffle.nodes.ReadNodeGen;
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
  @Child private ReadNode headNode = ReadNodeGen.create(Fragment.HEAD);

  public static final class Fine {
    private final Object constant;
    private final ReadNode constantRead;
    private final FragmentationNode parent;
    
    private Fine(Object constant, ReadNode constantRead, FragmentationNode parent) {
      this.constant = constant;
      this.constantRead = constantRead;
      this.parent = parent;
    }
  }
  
  protected static Fine[] registrationChecks(Registration r) {
    CompilerAsserts.neverPartOfCompilation();
    List<Fine> acc = new LinkedList<Fine>();
    while ( null != r.parent ) {
      acc.add(new Fine(
          r.noun, 
          ReadNodeGen.create(Fragment.HEAD), 
          new FragmentationNode(r.axisToParent)));
    }
    acc.add(new Fine(r.noun, null, null));
    return acc.toArray(new Fine[0]);
  }
  
  @ExplodeLoop
  protected static boolean fineCheck(Object noun, Fine[] checks) {
    CompilerAsserts.compilationConstant(checks);
    for ( Fine f : checks ) {
      if ( null == f.parent ) {
        return noun == f.constant;
      }
      else if ( !Noun.isCell(noun) ) {
        return false;
      }
      else if ( f.constantRead.executeRead(noun) != f.constant ) {
        return false;
      }
      noun = f.parent.executeFragment(noun);
    }
    return false; // never reached
  }
  
  @Specialization(
    limit  = "1",
    guards = { "driver != null",
               "fineCheck(core, checks)" })
  protected Object doJet(VirtualFrame frame, DynamicObject core,
      @Cached("getLocation(core)") Registration r,
      @Cached("registrationChecks(r)") Fine[] checks,
      @Cached("getDriver(r, getAxis())") ImplementationNode driver) {
    return driver.doJet(core);
  }

  @TruffleBoundary // hash operations
  protected Registration getLocation(DynamicObject core) {
    DynamicObject key = Noun.asCell(Cell.head(core));
    return getContext().locations.get(key);
  }

  @TruffleBoundary
  protected ImplementationNode getDriver(Registration loc, Object axis) {
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
  
  protected boolean batteryCheck(Object core, DynamicObject battery) {
    return headNode.executeRead(core) == battery;
  }
  
  protected DynamicObject getBattery(Object core) {
    return Noun.asCell(headNode.executeRead(core));
  }

  // Unregistered location, cached target, tail call
  @Specialization(
    limit = "1",
    guards = { "getInBattery()",
               "getIsTail()",
               "batteryCheck(core, cachedBattery)" })
  protected Object doCachedTail(DynamicObject core,
      @Cached("getBattery(core)") DynamicObject cachedBattery,
      @Cached("getLabel(cachedBattery)") KickLabel label,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getTarget(label, core, fragment)") CallTarget target) {
    throw new TailException(target, new Object[] { core });
  }
  
  protected FragmentationNode getFragmentationNode() {
    return new FragmentationNode(getAxis());
  }
  
  protected KickLabel getLabel(DynamicObject battery) {
    return new KickLabel(battery, getAxis());
  }

  @TruffleBoundary // hash operations
  protected CallTarget getTarget(KickLabel label,
      DynamicObject core,
      FragmentationNode fragment) {
    CompilerAsserts.neverPartOfCompilation();
    Context context = getContext();
    Map<KickLabel,CallTarget> kicks = context.kicks;
    CallTarget t = kicks.get(label);
    if ( null == t ) {
      DynamicObject formula = Noun.asCell(fragment.executeFragment(core));
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
               "batteryCheck(core, cachedBattery)" })
  protected Object doCachedCall(VirtualFrame frame, DynamicObject core,
      @Cached("getBattery(core)") DynamicObject cachedBattery,
      @Cached("getLabel(cachedBattery)") KickLabel label,
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
  protected Object doSlowTail(DynamicObject core,
      @Cached("getFragmentationNode()") FragmentationNode fragment) {
    KickLabel label = getLabel(getBattery(core));
    throw new TailException(getTarget(label, core, fragment), new Object[] { core });
  }

  // Unregistered location, varying target, direct call
  @Specialization(
    guards = { "getInBattery()" })
  protected Object doSlowCall(VirtualFrame frame, DynamicObject core,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getDispatch()") DispatchNode dispatch) {
    KickLabel label = getLabel(getBattery(core));
    return dispatch.call(frame, getTarget(label, core, fragment), new Object[] { core });
  }
  
  // kicked arm isn't even in the battery - treat as nock
  @Specialization
  protected Object doNock(VirtualFrame frame, DynamicObject core,
      @Cached("getFragmentationNode()") FragmentationNode fragment,
      @Cached("getNockDispatch()") NockDispatchNode dispatch) {
    DynamicObject formula = Noun.asCell(fragment.executeFragment(core));
    return dispatch.executeNock(frame, core, formula);
  }
  
  protected NockDispatchNode getNockDispatch() {
    return NockDispatchNodeGen.create(getContext(), getIsTail());
  }
  
}
