package net.frodwith.jaque.truffle.bloc;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Fragment;

public final class FragNode extends OpNode {
  @Children private final ReadOpNode[] reads;

  public FragNode (Axis axis) {
    Queue<ReadOpNode> q = new LinkedList<ReadOpNode>();
    for ( Fragment f : axis ) {
      if ( Fragment.HEAD == f ) {
        q.add(HeadOpNodeGen.create());
      }
      else {
        q.add(TailOpNodeGen.create());
      }
    }
    ReadOpNode[] r = new ReadOpNode[q.size()];
    this.reads = q.toArray(r);
  }

  @ExplodeLoop
  public void execute(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    Object r = s.pop();
    try {
      for ( ReadOpNode node : reads ) {
        r = node.executeRead(frame, r);
      }
      s.push(r);
    }
    catch ( UnsupportedSpecializationException e ) {
      throw new Bail();
    }
  }
}
