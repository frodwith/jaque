package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class ModNode extends BinaryOpNode {
  @Specialization
  protected long mod(long a, long b) {
    return Atom.mod(a, b);
  }

  @Specialization
  protected Object mod(int[] a, int[] b) {
    return Atom.mod(a, b);
  }
}