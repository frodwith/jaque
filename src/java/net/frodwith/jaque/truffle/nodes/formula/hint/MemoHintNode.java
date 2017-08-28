package net.frodwith.jaque.truffle.nodes.formula.hint;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

public final class MemoHintNode extends FormulaNode {
  private final Map<Object,Object> cache;
  @Child private FormulaNode next;
  
  public MemoHintNode(FormulaNode next) {
    this.next = next;
    this.cache = new HashMap<Object, Object>();
  }
  
  @TruffleBoundary
  private Object getCached(Object key) {
    return cache.get(key);
  }
  
  @TruffleBoundary
  private void setCached(Object key, Object value) {
    cache.put(key, value);
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object subject = getSubject(frame),
           pro = getCached(subject);
    if ( null == pro ) {
      pro = next.executeGeneric(frame);
      setCached(subject, pro);
    }
    return pro;
  }
}
