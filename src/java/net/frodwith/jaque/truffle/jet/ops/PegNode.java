package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class PegNode extends BinaryOpNode {
  @Specialization
  protected Object peg(Object a, Object b) {
    return Atom.peg(Atom.orBail(a), Atom.orBail(b));
  }
}