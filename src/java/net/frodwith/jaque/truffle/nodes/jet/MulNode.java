package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class MulNode extends BinaryJetNode {

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long mul(long a, long b) throws ArithmeticException {
    return Atom.mul(a, b);
  }

  @Specialization
  protected Object mul(int[] a, int[] b) {
    return Atom.mul(a, b);
  }

}