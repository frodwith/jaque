package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.Node.Child;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.dsl.Specialization;

@NodeInfo(shortName = "cons")
public abstract class ConsFormula extends Formula {
  @Child private Formula head;
  @Child private Formula tail;

  @Specialization
  protected Cell cons(Noun a, Noun b) {
    return new Cell(a, b);
  }

  public Cell toNoun() {
    return new Cell(head.toNoun(), tail.toNoun());
  }
}
