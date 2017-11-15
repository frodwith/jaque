package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class RipNode extends BinaryOpNode {
  @Specialization
  protected Object rip(long a, Object b) {
    return Atom.rip(Atom.bloqOrBail(a), Atom.orBail(b));
  }
}