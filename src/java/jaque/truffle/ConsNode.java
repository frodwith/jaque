package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

@NodeInfo(shortName = "cons")
public abstract class ConsNode extends Formula {
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
