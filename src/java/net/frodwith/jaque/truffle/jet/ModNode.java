package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class ModNode extends PairGateNode {

  @Specialization
  protected long mod(long a, long b) {
    return Atom.mod(a, b);
  }

  @Specialization
  protected Object mod(int[] a, int[] b) {
    return Atom.mod(a, b);
  }

}