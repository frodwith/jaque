package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class ConNode extends PairGateNode {

  @Specialization
  protected long con(long a, long b) {
    return Atom.con(a, b);
  }

  @Specialization
  protected Object con(int[] a, int[] b) {
    return Atom.con(a, b);
  }

}