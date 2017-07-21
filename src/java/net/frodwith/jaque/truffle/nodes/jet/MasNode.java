package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class MasNode extends GateNode {

  @Specialization
  protected long mas(long atom) {
    return (long) Atom.mas(atom);
  }

  @Specialization
  protected Object mas(int[] atom) {
    return Atom.mas(atom);
  }

}