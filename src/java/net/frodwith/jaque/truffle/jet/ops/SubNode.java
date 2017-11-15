package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

@NodeField(name="context", type=Context.class)
public abstract class SubNode extends BinaryOpNode {
  protected abstract Context getContext();

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