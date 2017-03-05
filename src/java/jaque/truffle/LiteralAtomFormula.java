package jaque.truffle;

import jaque.noun.Atom;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;


public class LiteralAtomFormula extends LiteralFormula {
  private final Atom a;

  public LiteralAtomFormula(Atom value) {
    this.a = value;
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    return a;
  }
  
  @Override
  public Atom executeAtom(VirtualFrame frame) {
    return a;
  }
  
  public Object getValue() {
    return a;
  }
}
