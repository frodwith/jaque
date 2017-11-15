package net.frodwith.jaque.truffle.jet.ops.crypt;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class EdSignNode extends BinaryOpNode {
  @Specialization
  protected Object sign(Object msg, Object sed) {
    return Atom.edSign(msg, sed);
  }
}