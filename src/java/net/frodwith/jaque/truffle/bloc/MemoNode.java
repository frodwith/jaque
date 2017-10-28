package net.frodwith.jaque.truffle.bloc;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class MemoNode extends OpNode {
  private final Map<Object,Object> cache;
  @Child private BlockNode body;

  public MemoNode(BlockNode body) {
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
