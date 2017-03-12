package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.KickException;
import net.frodwith.jaque.truffle.driver.Driver;

public abstract class KickDispatchNode extends JaqueNode {
  public abstract Object executeKick(VirtualFrame frame, Cell core, Object axis);
  
  public static Context getContext() {
    // TODO
    return null;
  }

  @Specialization(limit  = "1",
                  guards = { "!(driver == null)", 
                             "core.head == cachedBattery",
                             "fine(core)" })
  protected static Object doJet(Cell core, Object axis,
    @Cached("core.head") Object cachedBattery,
    @Cached("find(core, axis)") Driver driver) {
    return driver.apply(core);
  }

  @Specialization(replaces = "doJet")
  protected static Object doNock(Cell core, Object axis) {
    throw new KickException(core, axis);
  }
  
  protected static boolean fine(Cell core) {
    return getContext().fine(core);
  }
  
  protected static Driver find(Cell core, Object axis) {
    return getContext().find(core, axis);
  }
}