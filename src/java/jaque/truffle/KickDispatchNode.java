package jaque.truffle;

import jaque.noun.*;

import jaque.interpreter.Jet;

import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.dsl.Specialization;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;

public abstract class KickDispatchNode extends NockNode {
  public abstract Object executeKick(VirtualFrame frame, Cell core, Atom axis);
  
  /* TODO: integrate builtin nodes esp. for arithmetic, e.g. dec/add/sub/mul/div */

  @Specialization(limit  = "1",
                  guards = { "!(jet == null)", 
                             "core.getHead() == cachedBattery",
                             "getContext(frame).fine(core)" })
  protected static Object doJet(VirtualFrame frame, Cell core, Atom axis,
    @Cached("core.getHead()") Object cachedBattery,
    @Cached("getContext(frame).find(core, axis)") Jet jet,
    @Cached("jetFrags(jet)") Fragmenter[] frags)
  {
    Object[] arguments = new Object[frags.length];
    for ( int i = 0; i < frags.length; ++i ) {
      arguments[i] = frags[i].fragment(core);
    }
    return getContext(frame).apply(jet, arguments);
  }
  
  /* Many frequently kicked cores (kernels and the like) are in fact static. In
  * practice, however, kicks out of static cores are just consing a context onto
  * a constant gate. Caching this just slows us down.
  @Specialization(replaces = "doJet",
                  limit = "1",
                  guards = { "core == cachedCore" })
  protected static Object doStatic(VirtualFrame frame, Cell core, Atom axis,
    @Cached("core") Cell cachedCore,
    @Cached("create(getTarget(frame, core, axis))") DirectCallNode callNode,
    @Cached("staticCall(frame, callNode, core)") Object product)
  {
    return product;
  }
  */

  @Specialization(replaces = "doJet",
                  limit = "1",
                  guards = { "core.getHead() == cachedBattery" })
  protected static Object doCached(VirtualFrame frame, Cell core, Atom axis,
    @Cached("core.getHead()") Object cachedBattery,
    @Cached("getTarget(frame, core, axis)") CallTarget target)
  {
    throw new NockCallException(target, core);
  }

  @Specialization(replaces = "doCached")
  protected static Object doFirst(VirtualFrame frame, Cell core, Atom axis) {
    throw new NockCallException(getContext(frame).getKickTarget(core, axis), core);
  }
  
  protected static Fragmenter[] jetFrags(Jet j) {
    Atom[] axes = j.argumentLocations();
    Fragmenter[] frags = new Fragmenter[axes.length];
    for ( int i = 0; i < axes.length; ++i ) {
      frags[i] = new Fragmenter(axes[i]);
    }
    return frags;
  }
  
  protected static CallTarget getTarget(VirtualFrame frame, Cell core, Atom axis) {
    return getContext(frame).getKickTarget(core, axis);
  }
  
  protected static Object staticCall(VirtualFrame frame, DirectCallNode callNode, Cell core) {
    return callNode.call(frame, new Object[] { getContext(frame), core });
  }
}
