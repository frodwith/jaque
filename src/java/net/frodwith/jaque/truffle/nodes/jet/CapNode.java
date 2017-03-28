package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class CapNode extends UnaryGateNode {

  @Specialization
  protected Object cap(long atom) {
    return (long) Atom.cap(atom);
  }

  @Specialization
  protected Object cap(int[] atom) {
    return (long) Atom.cap(atom);
  }

}