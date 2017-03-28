package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class LteNode extends BinaryJetNode {

  @Specialization
  protected long lte(long a, long b) {
    return (Atom.compare(a, b) < 1) ? Atom.YES : Atom.NO;
  }

  @Specialization
  protected long lte(int[] a, int[] b) {
    return (Atom.compare(a, b) < 1) ? Atom.YES : Atom.NO;
  }

}