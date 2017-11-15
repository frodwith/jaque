package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.UnaryOpNode;

public abstract class BexNode extends UnaryOpNode {
  @Specialization
  protected Object bex(long atom) {
    return Atom.bex(atom);
  }
}