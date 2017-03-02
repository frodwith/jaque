package jaque.truffle;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;

import jaque.noun.Cell;

public class MemoHintFormula extends Formula {
  @Child private Formula f;
  private Map<Object,Object> cache;
  
  public MemoHintFormula(Formula next) {
    this.f = next;
    this.cache = new HashMap<Object, Object>();
  }

  @Override
  public Cell toCell() {
    return new Cell(10, new Cell(MEMO, f.toCell()));
  }

  @Override
  public Object execute(VirtualFrame frame) {
    Object r, s = getSubject(frame);
    if ( cache.containsKey(s) ) {
      r = cache.get(s);
    }
    else {
      r = f.execute(frame);
      cache.put(s, r);
    }
    return r;
  }

}
