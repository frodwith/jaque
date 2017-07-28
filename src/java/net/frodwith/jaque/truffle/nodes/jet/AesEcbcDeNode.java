package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class AesEcbcDeNode extends AesEcbNode {

  @Specialization
  protected Object de(Object key, Object block) {
    return Atom.aes_ecbc_de(key, block);
  }

}