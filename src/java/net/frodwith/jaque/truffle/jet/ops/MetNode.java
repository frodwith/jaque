package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class MetNode extends BinaryOpNode {
  @Specialization
  protected long met(long a, Object b) {
    return Atom.met(Atom.bloqOrBail(a), Atom.orBail(b));
  }
}