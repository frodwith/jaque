package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class DivNode extends PairGateNode {

  @Specialization
  protected long div(long a, long b) {
    return Atom.div(a, b);
  }

  @Specialization
  protected Object div(int[] a, int[] b) {
    return Atom.div(a, b);
  }

}