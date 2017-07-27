package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class AddNode extends PairGateNode {

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long add(long a, long b) {
    return Atom.add(a, b);
  }

  @Specialization
  protected Object add(int[] a, int[] b) {
    return Atom.add(a, b);
  }

}