package net.frodwith.jaque.truffle.nodes.formula.hint;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.nodes.formula.Formula;
import net.frodwith.jaque.truffle.nodes.formula.JumpFormula;

public class MemoHintNode extends JumpFormula {
  private final Map<Object,Object> cache;
  @Child private Formula next;
  
  public MemoHintNode(Formula next) {
    this.next = next;
    this.cache = new HashMap<Object, Object>();
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object subject = getSubject(frame);
    if ( cache.containsKey(subject) ) {
      return cache.get(subject);
    }
    else {
      Object product = next.executeSafe(frame);
      cache.put(subject, product);
      return product;
    }
  }
}
