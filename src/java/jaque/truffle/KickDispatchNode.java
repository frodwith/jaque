package jaque.truffle;

import jaque.noun.*;

import jaque.interpreter.Jet;

import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.dsl.Specialization;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;

public abstract class KickDispatchNode extends NockNode {

  public abstract Object executeKick(VirtualFrame frame, Cell core, Atom axis);
  
  /* TODO: integrate builtin nodes esp. for arithmetic, e.g. dec/add/sub/mul/div */

  @Specialization(limit  = "1",
                  guards = {"!(jet == null)",
                            "core.p == cachedBattery",
                            "getContext(frame).fineCore(core)"})
  protected static Noun doJet(VirtualFrame frame, Cell core, Atom axis,
    @Cached("core.p") Noun cachedBattery,
    @Cached("getContext(frame).findJet(core, axis)") Jet jet)
  {
    return getContext(frame).applyJet(jet, core);
  }

  @Specialization(limit    = "1",
                  replaces = "doJet",
                  guards   = {"core.p == cachedBattery"})
  protected static Object doDirect(VirtualFrame frame, Cell core, Atom axis,
    @Cached("core.p") Noun cachedBattery,
    @Cached("create(getContext(frame).getKickTarget(core, axis))") DirectCallNode callNode)
  {
    return callNode.call(frame, new Object[] {getContext(frame), core});
  }

  @Specialization(replaces = "doDirect")
  protected static Object doIndirect(VirtualFrame frame, Cell core, Atom axis,
    @Cached("create()") IndirectCallNode callNode)
  {
    CallTarget target = getContext(frame).getKickTarget(core, axis);
    return callNode.call(frame, target, new Object[] {getContext(frame), core});
  }
}
