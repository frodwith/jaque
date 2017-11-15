package net.frodwith.jaque.truffle.jet.ops.crypt;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class AesEcbbEnNode extends BinaryOpNode {
  @Specialization
  protected Object en(Object key, Object block) {
    return Atom.aes_ecbb_en(key, block);
  }
}