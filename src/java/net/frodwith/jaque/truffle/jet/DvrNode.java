package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;

public abstract class DvrNode extends PairGateNode {

  @Specialization
  protected Cell dvr(long a, long b) {
    return Atom.dvr(a, b);
  }

  @Specialization
  protected Cell dvr(int[] a, int[] b) {
    return Atom.dvr(a, b);
  }

}