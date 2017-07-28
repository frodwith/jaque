package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class AesEcbbDeNode extends AesEcbNode {

  @Specialization
  protected Object de(Object key, Object block) {
    return Atom.aes_ecbb_de(key, block);
  }

}