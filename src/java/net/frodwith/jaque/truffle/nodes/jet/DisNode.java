package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class DisNode extends PairGateNode {

  @Specialization
  protected long dis(long a, long b) {
    return Atom.dis(a, b);
  }

  @Specialization
  protected Object dis(int[] a, int[] b) {
    return Atom.dis(a, b);
  }

}