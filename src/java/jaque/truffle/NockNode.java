package jaque.truffle;

import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;

import jaque.interpreter.Bail;
import jaque.noun.*;

@TypeSystemReference(NockTypes.class)
public abstract class NockNode extends Node {
  protected static NockContext getContext(VirtualFrame frame) {
    return (NockContext) frame.getArguments()[0];
  }
  
  protected static Object getSubject(VirtualFrame frame) {
    return frame.getArguments()[1];
  }
  
  @ExplodeLoop
  public static Object fragment(Atom axis, Object r) {
    for ( Boolean tail : axis.fragments() ) {
      if ( !(r instanceof Cell) ) {
        throw new Bail();
      }
      else if ( tail.booleanValue() ) {
        r = ((Cell) r).getTail();
      }
      else {
        r = ((Cell) r).getHead();
      }
    }
    return r;
  }
}
