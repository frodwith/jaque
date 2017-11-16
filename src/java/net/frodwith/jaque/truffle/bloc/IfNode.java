package net.frodwith.jaque.truffle.bloc;

import java.util.Deque;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ConditionProfile;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.TypesGen;

public class IfNode extends FlowNode {
  private CallTarget yes, no;
  private final ConditionProfile test = ConditionProfile.createCountingProfile();
  
  public IfNode(CallTarget yes, CallTarget no) {
    this.yes = yes;
    this.no = no;
  }

  @Override
  public Continuation execute(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    try {
      long loob = TypesGen.expectLong(s.pop());
      if ( 1L == loob || 0L == loob ) {
        if ( test.profile(Atom.YES == loob) ) {
          return Continuation.call(yes, after);
        }
        else {
          return Continuation.call(no, after);
        }
      }
      else {
        throw new Bail();
      }
    }
    catch ( UnexpectedResultException e) {
      throw new Bail();
    }
  }

}
