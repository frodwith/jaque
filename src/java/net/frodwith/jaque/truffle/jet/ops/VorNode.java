package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class VorNode extends BinaryOpNode {
  @Specialization
  protected long vor(Object a, Object b) {
    return Atom.vor(a, b);
  }
}