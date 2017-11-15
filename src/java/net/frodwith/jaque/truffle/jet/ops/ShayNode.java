package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class ShayNode extends BinaryOpNode {
  @Specialization
  protected Object shay(Object len, Object atom) {
    return Atom.shay(len, atom);
  }
}