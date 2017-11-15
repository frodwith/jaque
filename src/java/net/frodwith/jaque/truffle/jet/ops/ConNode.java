package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class ConNode extends BinaryOpNode {
  @Specialization
  protected long con(long a, long b) {
    return Atom.con(a, b);
  }

  @Specialization
  protected Object con(int[] a, int[] b) {
    return Atom.con(a, b);
  }
}