package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class GorNode extends BinaryOpNode {
  @Specialization
  protected long gor(Object a, Object b) {
    return Atom.gor(a, b);
  }
}