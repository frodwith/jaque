package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.UnaryOpNode;

public abstract class CapNode extends UnaryOpNode {
  @Specialization
  protected long cap(long atom) {
    return Atom.cap(atom);
  }

  @Specialization
  protected long cap(int[] atom) {
    return Atom.cap(atom);
  }
}