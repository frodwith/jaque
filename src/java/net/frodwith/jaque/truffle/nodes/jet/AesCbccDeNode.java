package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class AesCbccDeNode extends AesCbcNode {

  @Specialization
  protected Object de(Object key, Object iv, Object msg) {
    return Atom.aes_cbcc_de(key, iv, msg);
  }

}