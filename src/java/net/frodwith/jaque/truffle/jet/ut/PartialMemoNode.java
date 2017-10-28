package net.frodwith.jaque.truffle.jet.ut;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.jet.ImplementationNode;

public abstract class PartialMemoNode extends ImplementationNode {
  public abstract Cell executeMemoKey(Cell core);
  private static final Map<Cell,Object> memo = new HashMap<Cell, Object>();
  protected static final Axis vanVet = new Axis(118L);
  protected static final Axis vanVrf = new Axis(59L);
  
  protected static long tip(String mote, Object van) {
    try {
      long tip = 144 + Atom.mote(mote);
      if ( vanVet.fragment(van).equals(Atom.NO) ) {
        tip += 256;
      }
      return tip;
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }
  
  @TruffleBoundary
  private Object recall(Cell key) {
    return memo.get(key);
  }
  
  @TruffleBoundary
  private void store(Cell key, Object value) {
    memo.put(key, value);
  }

  @Override
  public Object doJet(VirtualFrame frame, Object core) {
    Cell key = executeMemoKey(Cell.orBail(core));
    //String wat = getClass().getSimpleName().substring(0, 4).toLowerCase();
    Object mem = recall(key);
    if ( null == mem ) {
      mem = getFallback().call(frame, new Object[] { core });
      store(key, mem);
    }
    return mem;
  }
}
