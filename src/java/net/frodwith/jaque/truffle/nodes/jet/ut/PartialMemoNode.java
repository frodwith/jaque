package net.frodwith.jaque.truffle.nodes.jet.ut;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.FragmentationException;
import net.frodwith.jaque.truffle.nodes.DispatchNode;
import net.frodwith.jaque.truffle.nodes.DispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;
import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public abstract class PartialMemoNode extends ImplementationNode {
  public abstract Cell executeMemoKey(Cell core);
  private static final Map<Cell,Object> memo = new HashMap<Cell, Object>();
  private DispatchNode dispatch = null;
  protected static final FragmentationNode vanVet = new FragmentationNode(118L);
  protected static final FragmentationNode vanVrf = new FragmentationNode(59L);
  
  protected static long tip(String mote, Object van) {
    try {
      long tip = 144 + Atom.mote(mote);
      if ( vanVet.executeFragment(van).equals(Atom.NO) ) {
        tip += 256;
      }
    return tip;
    }
    catch ( FragmentationException e ) {
      throw new Bail();
    }
  }

  @Override
  public Object doJet(VirtualFrame frame, Object core) {
    Cell key = executeMemoKey(Cell.expect(core));
    if ( memo.containsKey(key) ) {
      System.err.println("memo: hit");
      return memo.get(key);
    }
    else {
      System.err.println("memo: miss");
      if ( null == dispatch ) { // FIXME: there is a way to do this on node creation
        dispatch = DispatchNodeGen.create();
        insert(dispatch);
      }
      Object pro = dispatch.call(frame, getFallback(), new Object[] { core });
      memo.put(key, pro);
      return pro;
    }
  }
}
