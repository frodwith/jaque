package net.frodwith.jaque.truffle.nodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TailException;
import net.frodwith.jaque.truffle.nodes.jet.JetNode;

@NodeField(name="context", type=Context.class)
public abstract class KickDispatchNode extends JaqueNode {

  public abstract Object executeKick(Cell core, Object axis);
  public abstract Context getContext();
  
  @Specialization(limit  = "1",
                  guards = { "!(jetNode == null)", 
                             "matchBattery(core, cachedBattery, axis)",
                             "fine(core)" })
  protected Object doJet(Cell core, Object axis,
    @Cached("core.head") Object cachedBattery,
    @Cached("find(core, axis)") JetNode jetNode) {
    return jetNode.doJet(core);
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
    Context context = getContext();
    Class<? extends JetNode> driver = context.find(core, axis);
    if ( null == driver ) {
      return null;
    }
    else {
      try {
        CompilerDirectives.transferToInterpreter();
        Constructor<? extends JetNode> cons = driver.getConstructor(Context.class);
        return cons.newInstance(context);
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