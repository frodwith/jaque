package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class DvrNode extends BinaryOpNode {
  @Specialization
  protected Cell dvr(long a, long b) {
    return Atom.dvr(a, b);
  }
  @Specialization
  protected Cell dvr(int[] a, int[] b) {
    return Atom.dvr(a, b);
  }
}