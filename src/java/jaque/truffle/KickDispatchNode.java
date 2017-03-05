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
    @Cached("getContext(frame).find(core, axis)") Jet jet)
  {
    return getContext(frame).apply(jet, core);
  }

  @Specialization(replaces = "doJet")
  protected static Object doNock(VirtualFrame frame, Cell core, Atom axis) {
    throw new NockCallException(getContext(frame).getKickTarget(core, axis), core);
  }
}
