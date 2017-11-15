package net.frodwith.jaque.truffle.jet.ops.crypt;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class AesEcbbDeNode extends BinaryOpNode {
  @Specialization
  protected Object de(Object key, Object block) {
    return Atom.aes_ecbb_de(key, block);
  }
}