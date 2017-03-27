package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class DivNode extends BinaryJetNode {

  @Specialization
  protected long div(long a, long b) {
    return Atom.div(a, b);
  }

  @Specialization
  protected Object div(int[] a, int[] b) {
    return Atom.div(a, b);
  }

}