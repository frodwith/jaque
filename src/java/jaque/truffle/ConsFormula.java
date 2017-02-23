package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

@NodeInfo(shortName = "cons")
@NodeChildren({@NodeChild("head"), @NodeChild("tail")})
public abstract class ConsFormula extends Formula {
  public abstract Formula getHead();
  public abstract Formula getTail();

  @Specialization
  protected Cell cons(Noun a, Noun b) {
    return new Cell(a, b);
  }

  public Cell toNoun() {
    return new Cell(getHead().toNoun(), getTail().toNoun());
  }
}
