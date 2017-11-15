package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class MixNode extends BinaryOpNode {
  @Specialization
  protected long mix(long a, long b) {
    return Atom.mix(a, b);
  }

  @Specialization
  protected Object mix(int[] a, int[] b) {
    return Atom.mix(a, b);
  }
}