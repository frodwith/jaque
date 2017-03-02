package jaque.truffle;

import com.oracle.truffle.api.nodes.NodeInfo;

import jaque.noun.Cell;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

@NodeInfo(shortName = "cons")
@NodeChildren({@NodeChild("head"), @NodeChild("tail")})
public abstract class ConsFormula extends Formula {
  public abstract Formula getHead();
  public abstract Formula getTail();

  @Specialization
  protected Cell cons(Object a, Object b) {
    return new Cell(a, b);
  }

  public Cell toCell() {
    return new Cell(getHead().toCell(), getTail().toCell());
  }
}
