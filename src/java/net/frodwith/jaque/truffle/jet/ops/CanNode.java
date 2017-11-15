package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class CanNode extends BinaryOpNode {
  @Specialization
  protected Object can(long a, Object b) {
    return Atom.can(Atom.bloqOrBail(a), new List(b));
  }
}