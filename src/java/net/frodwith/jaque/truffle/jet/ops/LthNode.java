package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class LthNode extends BinaryOpNode {
  @Specialization
  protected long lth(long a, long b) {
    return (Atom.compare(a, b) == -1) ? Atom.YES : Atom.NO;
  }

  @Specialization
  protected long lth(int[] a, int[] b) {
    return (Atom.compare(a, b) == -1) ? Atom.YES : Atom.NO;
  }
}