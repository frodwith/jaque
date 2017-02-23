package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

@NodeInfo(shortName = "deep")
@NodeChild("f")
public abstract class DeepFormula extends Formula {
  public abstract Formula getF();

  @Specialization
  protected boolean deep(Atom v) {
    return false;
  }

  @Specialization
  protected boolean deep(Cell v) {
    return true;
  }

  @Specialization
  protected boolean deep(Noun v) {
    return (v instanceof Cell);
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(3), getF().toNoun());
  }
}
