package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class ShalNode extends BinaryOpNode {
  @Specialization
  protected Object shal(Object len, Object atom) {
    return Atom.shal(len, atom);
  }
}