package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class MixNode extends PairGateNode {

  @Specialization
  protected long mix(long a, long b) {
    return Atom.mix(a, b);
  }

  @Specialization
  protected Object mix(int[] a, int[] b) {
    return Atom.mix(a, b);
  }

}