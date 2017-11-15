package net.frodwith.jaque.truffle.jet;

import java.util.Stack;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.Continuation;

public abstract class UtNode extends ImplementationNode {
  public final Context context;
  public final CallTarget fallback;
  
  protected static final long verTip = 151L;
  protected static final Axis vanVet = new Axis(118L);
  protected static final Axis vanVrf = new Axis(59L);
  
  public abstract Cell getKey(Cell core) throws UnexpectedResultException;
  
  protected static long tip(String mote, Object van) {
    try {
      long tip = verTip + Atom.mote(mote);
      if ( vanVet.fragment(van).equals(Atom.NO) ) {
        tip += 256;
      }
      return tip;
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }

  protected UtNode(Context context, CallTarget fallback) {
    this.context = context;
    this.fallback = fallback;
  }
  
  public Continuation executeJet(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    try {
      Cell core       = Cell.expect(s.pop());
      Cell key        = getKey(core);
      Object product  = context.getMemo(key);
      if ( null == product ) {
        s.push(key);
        s.push(core);
        return Continuation.call(fallback, context.SAVE_MEMO);
      }
      else {
        s.push(product);
        return Continuation.ret();
      }
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }
}
