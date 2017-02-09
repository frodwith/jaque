package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

@NodeInfo(shortName = "same")
public final class SameNode extends Formula {
  @Child private Formula a;
  @Child private Formula b;

  @Specialization
  protected boolean same(boolean a, boolean b) {
    return a == b;
  }

  @Specialization
  protected boolean same(long a, long b) {
    return a == b;
  }
  
  @Specialization
  protected boolean same(Atom a, Atom b) {
    return a.equals(b);
  }

  @Specialization
  protected boolean same(Cell a, Cell b) {
    a.equals(b);
  }

  @Specialization(guards = {"a.getClass() != b.getClass()"})
  protected boolean same(Object a, Object b) {
    return false;
  }

  public Cell toNoun() {
    return new Cell(Atom.fromLong(5), new Cell(headF.toNoun(), tailF.toNoun()));
  }
}
