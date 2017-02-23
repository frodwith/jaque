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
  private static final int CONTEXT_INDEX = 0;
  private static final int SUBJECT_INDEX = 1;
  private static final int KICKREC_INDEX = 2;

  protected static NockContext getContext(VirtualFrame frame) {
    return (NockContext) frame.getArguments()[CONTEXT_INDEX];
  }
  
  protected static Noun getSubject(VirtualFrame frame) {
    return (Noun) frame.getArguments()[SUBJECT_INDEX];
  }
  
  @SuppressWarnings("unchecked")
  protected static Map<KickLabel,CallTarget> getKickRecord(VirtualFrame frame) {
    return (Map<KickLabel,CallTarget>) frame.getArguments()[KICKREC_INDEX];
  }
  
  @ExplodeLoop
  public static Noun fragment(Atom axis, Noun r) {
    for ( Boolean tail : axis.fragments() ) {
      if ( !(r instanceof Cell) ) {
        throw new Bail();
      }
      else if ( tail.booleanValue() ) {
        r = ((Cell) r).q;
      }
      else {
        r = ((Cell) r).p;
      }
    }
    return r;
  }
}
