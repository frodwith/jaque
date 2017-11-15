package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class MulNode extends BinaryOpNode {
  @Specialization(rewriteOn = ArithmeticException.class)
  protected long mul(long a, long b) throws ArithmeticException {
    return Atom.mul(a, b);
  }

  @Specialization
  protected Object mul(int[] a, int[] b) {
    return Atom.mul(a, b);
  }
}