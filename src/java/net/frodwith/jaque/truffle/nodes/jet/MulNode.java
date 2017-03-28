package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class MulNode extends PairGateNode {

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long mul(long a, long b) throws ArithmeticException {
    return Atom.mul(a, b);
  }

  @Specialization
  protected Object mul(int[] a, int[] b) {
    return Atom.mul(a, b);
  }

}