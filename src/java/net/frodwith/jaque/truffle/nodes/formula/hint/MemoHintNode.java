package net.frodwith.jaque.truffle.nodes.formula.hint;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

public class MemoHintNode extends FormulaNode {
  private final Map<Object,Object> cache;
  @Child private FormulaNode next;
  
  public MemoHintNode(FormulaNode next) {
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
      Object product = next.executeGeneric(frame);
      cache.put(subject, product);
      return product;
    }
  }
}
