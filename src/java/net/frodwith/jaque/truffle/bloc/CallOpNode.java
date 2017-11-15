package net.frodwith.jaque.truffle.bloc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.jet.Definition;
import net.frodwith.jaque.truffle.jet.ImplementationNode;

public abstract class CallOpNode extends BlocNode {
  
  public abstract Continuation executeCall(VirtualFrame frame, Context context, Continuation after, Object core, Axis axis);
  
  @Specialization(
    guards = { "driver != null",
               "same(core.head, cachedBattery)",
               "fineNode.executeFine(frame, core)" })
  protected Continuation jet(VirtualFrame frame, Context context, Continuation after, Cell core, Axis axis,
      @Cached("core.head") Object cachedBattery,
      @Cached("getLocation(context, core)") Location location,
      @Cached("getDriver(context, axis, location, core)") CallTarget driver,
      @Cached("getFineNode(location)") FineCheckNode fineNode,
      @Cached("getContinuation(driver, after)") Continuation cont) {
    return cont;
  }
  
  @Specialization(guards = { "same(core.head, cachedBattery)" })
  protected Continuation cached(VirtualFrame frame, Context context, Continuation after, Cell core, Axis axis,
      @Cached("core.head") Object cachedBattery,
      @Cached("unjetted(context, core, axis)") CallTarget target,
      @Cached("getContinuation(target, after)") Continuation cont) {
    return cont;
  }
  
  @Specialization
  protected Continuation uncached(VirtualFrame frame, Context context, Continuation after, Cell core, Axis axis) {
    return getContinuation(unjetted(context, core, axis), after);
  }
  
  protected static boolean same(Object a, Object b) {
    return Noun.equals(a, b);
  }
  
  protected static Continuation getContinuation(CallTarget target, Continuation k) {
    if ( null == k.target ) {
      return Continuation.jump(target);
    }
    else if ( null == k.after ) {
      return Continuation.call(target, k.target);
    }
    else {
      // k is only a continuation because null doesn't pass instanceof, it really can only
      // represent ret or jump, not a full call
      assert(false);
      return null;
    }
  }
  
  protected static FineCheckNode getFineNode(Location loc) {
    return (loc == null) ? null : new FineCheckNode(loc);
  }
  
  @TruffleBoundary
  protected static Location getLocation(Context context, Cell core) {
    CompilerAsserts.neverPartOfCompilation();
    return context.locations.get(core.head);
  }
  
  protected static CallTarget unjetted(Context context, Cell core, Axis axis) {
    try {
      return context.evalTarget(TypesGen.expectCell(axis.fragment(core)));
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }
  
  @TruffleBoundary
  protected CallTarget getDriver(Context context, Axis axis, Location loc, Cell core) {
    CompilerAsserts.neverPartOfCompilation();
    if ( null == loc ) {
      return null;
    }
    else {
      Class<? extends Definition> klass = context.getDriver(loc, axis.atom);
      if ( null == klass ) {
        return null;
      }
      else {
        Definition def;
        try {
          def = klass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
          return null;
        }
        ImplementationNode impl = def.createNode(context, unjetted(context, core, axis));
        String name = loc.label + ":" + Atom.toString(axis.atom);
        JetRootNode root = new JetRootNode(name, impl);
        return Truffle.getRuntime().createCallTarget(root);
      }
    }
  }
}
