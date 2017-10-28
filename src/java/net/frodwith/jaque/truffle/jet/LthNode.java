package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class LthNode extends PairGateNode {

  @Specialization
  protected long lth(long a, long b) {
    return (Atom.compare(a, b) == -1) ? Atom.YES : Atom.NO;
  }

  @Specialization
  protected long lth(int[] a, int[] b) {
    return (Atom.compare(a, b) == -1) ? Atom.YES : Atom.NO;
  }

}