package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;

public abstract class SubNode extends PairGateNode {
  @Specialization(rewriteOn = ArithmeticException.class)
  protected long sub(long a, long b) {
    try {
      return Atom.sub(a, b);
    }
    catch (Bail e) {
      getContext().err("subtract underflow");
      throw e;
    }
  }

  @Specialization
  protected int[] sub(int[] a, int[] b) {
    try {
      return Atom.sub(a, b);
    }
    catch (Bail e) {
      getContext().err("subtract underflow");
      throw e;
    }
  }
}