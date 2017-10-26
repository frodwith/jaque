package net.frodwith.jaque.truffle.bloc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Clue;
import net.frodwith.jaque.data.Fragment;
import net.frodwith.jaque.truffle.Context;

public final class MemoNode extends OpNode {
  private final Context context;
  private final Map<Object,Object> cache;
  @Child private BlockNode body;

  public MemoNode(Context context, BlockNode body) {
    this.context = context;
    this.body = body;
    this.cache = new HashMap<Object,Object>();
  }
  
  @TruffleBoundary
  public Object get(Object subject) {
    return cache.get(subject);
  }

  @TruffleBoundary
  public Object set(Object subject, Object value) {
    return cache.put(subject, value);
  }
  
  public void execute(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    Object subject = s.peek();
    Object pro = get(subject);
    if ( null == pro ) {
      // memo hints consume java stack! ("fixable", low priority/undesirable)
      body.execute(frame);
      set(subject, s.peek());
    }
    else {
      s.pop();
      s.push(pro);
    }
  }
}
