package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class CapNode extends UnaryGateNode {

  @Specialization
  protected long cap(long atom) {
    return Atom.cap(atom);
  }

  @Specialization
  protected long cap(int[] atom) {
    return Atom.cap(atom);
  }

}