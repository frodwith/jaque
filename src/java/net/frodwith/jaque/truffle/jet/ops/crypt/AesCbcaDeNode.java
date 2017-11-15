package net.frodwith.jaque.truffle.jet.ops.crypt;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.TernaryOpNode;

public abstract class AesCbcaDeNode extends TernaryOpNode {
  @Specialization
  protected Object de(Object key, Object iv, Object msg) {
    return Atom.aes_cbca_de(key, iv, msg);
  }
}