package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class ModNode extends BinaryJetNode {

  @Specialization
  protected long mod(long a, long b) {
    return Atom.mod(a, b);
  }

  @Specialization
  protected Object mod(int[] a, int[] b) {
    return Atom.mod(a, b);
  }

}