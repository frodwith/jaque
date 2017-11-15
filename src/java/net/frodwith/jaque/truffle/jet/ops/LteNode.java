package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class LteNode extends BinaryOpNode {
  @Specialization
  protected long lte(long a, long b) {
    return (Atom.compare(a, b) < 1) ? Atom.YES : Atom.NO;
  }

  @Specialization
  protected long lte(int[] a, int[] b) {
    return (Atom.compare(a, b) < 1) ? Atom.YES : Atom.NO;
  }
}