package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class DorNode extends BinaryOpNode {
  @Specialization
  protected long dor(Object a, Object b) {
    return Atom.dor(a, b);
  }
}