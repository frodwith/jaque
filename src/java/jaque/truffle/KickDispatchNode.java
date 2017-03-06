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
      arguments[i] = Noun.coerceNoun(frags[i].fragment(core));
    }
    return getContext(frame).apply(jet, arguments);
  }

  @Specialization(replaces = "doJet",
                  limit = "1",
                  guards = { "core.getHead() == cachedBattery" })
  protected static Object doCached(VirtualFrame frame, Cell core, Atom axis,
    @Cached("core.getHead()") Object cachedBattery,
    @Cached("getContext(frame).getKickTarget(core, axis)") CallTarget target)
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
}
