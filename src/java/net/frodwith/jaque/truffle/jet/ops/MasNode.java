package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.UnaryOpNode;

public abstract class MasNode extends UnaryOpNode {
  @Specialization
  protected long mas(long atom) {
    return (long) Atom.mas(atom);
  }

  @Specialization
  protected Object mas(int[] atom) {
    return Atom.mas(atom);
  }
}