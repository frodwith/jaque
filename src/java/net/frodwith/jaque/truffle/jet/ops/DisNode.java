package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class DisNode extends BinaryOpNode {
  @Specialization
  protected long dis(long a, long b) {
    return Atom.dis(a, b);
  }

  @Specialization
  protected Object dis(int[] a, int[] b) {
    return Atom.dis(a, b);
  }
}