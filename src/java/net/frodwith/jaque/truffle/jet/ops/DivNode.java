package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class DivNode extends BinaryOpNode {
  @Specialization
  protected long div(long a, long b) {
    return Atom.div(a, b);
  }

  @Specialization
  protected Object div(int[] a, int[] b) {
    return Atom.div(a, b);
  }
}