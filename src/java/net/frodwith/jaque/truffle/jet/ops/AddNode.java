package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class AddNode extends BinaryOpNode {

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long add(long a, long b) throws ArithmeticException {
    return Atom.add(a, b);
  }

  @Specialization
  protected Object add(int[] a, int[] b) {
    return Atom.add(a, b);
  }
}
