package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class GteNode extends BinaryJetNode {

  @Specialization
  protected long gte(long a, long b) {
    return (Atom.compare(a, b) > - 1) ? Atom.YES : Atom.NO;
  }

  @Specialization
  protected long gte(int[] a, int[] b) {
    return (Atom.compare(a, b) > -1) ? Atom.YES : Atom.NO;
  }

}