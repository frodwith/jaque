package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class BumpNode extends UnaryFormulaNode {

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long bump(long atom) throws ArithmeticException {
    return Atom.increment(atom);
  }
  
  @Specialization
  protected int[] bump(int[] atom) {
    return Atom.increment(atom);
  }

}