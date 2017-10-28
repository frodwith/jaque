package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class CapNode extends GateNode {

  @Specialization
  protected long cap(long atom) {
    return Atom.cap(atom);
  }

  @Specialization
  protected long cap(int[] atom) {
    return Atom.cap(atom);
  }

}