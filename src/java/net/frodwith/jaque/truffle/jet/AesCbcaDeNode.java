package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class AesCbcaDeNode extends AesCbcNode {

  @Specialization
  protected Object de(Object key, Object iv, Object msg) {
    return Atom.aes_cbca_de(key, iv, msg);
  }

}