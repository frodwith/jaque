package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.NockLanguage;
import net.frodwith.jaque.truffle.TailException;
import net.frodwith.jaque.truffle.nodes.jet.JetNode;

public abstract class KickDispatchNode extends JaqueNode {
  @Child private Node contextNode;

  protected KickDispatchNode() {
    this.contextNode = NockLanguage.INSTANCE.contextNode();
  }

  public abstract Object executeKick(Cell core, Object axis);
  
  public Context getContext() {
    return NockLanguage.INSTANCE.context(contextNode);
  }
  
  @Specialization(limit  = "1",
                  guards = { "!(jetNode == null)", 
                             "matchBattery(core, cachedBattery, axis)",
                             "fine(core)" })
  protected Object doJet(Cell core, Object axis,
    @Cached("core.head") Object cachedBattery,
    @Cached("find(core, axis)") JetNode jetNode) {
    return jetNode.executeJet(core);
  }
  
  @Specialization(replaces = "doJet",
      limit = "1",
      guards = { "matchBattery(core, cachedBattery, axis)" })
  protected Object doCached(Cell core, Object axis,
    @Cached("core.head") Object cachedBattery,
    @Cached("getTarget(core, axis)") CallTarget target) {
    throw new TailException(target, core);
  }

  @Specialization(replaces = "doCached")
  protected Object doSlow(Cell core, Atom axis) {
    throw new TailException(getTarget(core, axis), core);
  }
  
  protected boolean matchBattery(Cell core, Object battery, Object axis) {
    return (Atom.cap(axis) == 2) && (core.head == battery);
  }
  
  protected boolean fine(Cell core) {
    return getContext().fine(core);
  }
  
  protected JetNode find(Cell core, Object axis) {
    Class<? extends JetNode> driver = getContext().find(core, axis);
    if ( null == driver ) {
      return null;
    }
    else {
      try {
        CompilerDirectives.transferToInterpreter();
        return driver.newInstance();
      }
      catch (IllegalAccessException e) {
        e.printStackTrace();
        return null;
      }
      catch (InstantiationException e) {
        e.printStackTrace();
        return null;
      }
    }
  }
  
  protected CallTarget getTarget(Cell core, Object axis) {
    return getContext().getKick(core, axis);
  }
}