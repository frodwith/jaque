package net.frodwith.jaque.truffle.bloc;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Axis;

public final class FineCheckNode extends BlocNode {
  @Children private FineOpNode[] checks;
  @Children private FragNode[] frags;

  public FineCheckNode(Location loc) {
    if ( loc.isStatic ) {
      this.checks = new FineOpNode[] { StaticFineNodeGen.create(loc.noun) };
      this.frags = null;
    }
    else {
      Queue<FineOpNode> fine = new LinkedList<FineOpNode>();
      Queue<FragNode> frag = new LinkedList<FragNode>();
      // skip battery check for first location
      frag.add(new FragNode(new Axis(loc.axisToParent)));
      loc = loc.parent;
      while ( !loc.isStatic ) {
        fine.add(DynamicFineNodeGen.create(loc.noun));
        frag.add(new FragNode(new Axis(loc.axisToParent)));
        loc = loc.parent;
      }
      assert( loc.isStatic );
      fine.add(StaticFineNodeGen.create(loc.noun));
      this.checks = fine.toArray(new FineOpNode[fine.size()]);
      this.frags = frag.toArray(new FragNode[frag.size()]);
    }
  }
  
  @ExplodeLoop
  public boolean executeFine(VirtualFrame frame, Object core) {
    try {
      if ( null == frags ) {
        assert( checks.length == 1 );
        return checks[0].executeFine(frame, core);
      }
      else {
        Stack<Object> s = getStack(frame);
        boolean pass = true;
        s.push(core);
        for ( int i = 0; i < frags.length; ++i ) {
          frags[i].execute(frame);
          if ( !checks[i].executeFine(frame, s.peek()) ) {
            pass = false;
            break;
          }
        }
        s.pop();
        return pass;
      }
    }
    catch ( UnsupportedSpecializationException e ) {
      return false;
    }
  }

}
