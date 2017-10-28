package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class AesEcbaDeNode extends AesEcbNode {

  @Specialization
  protected Object de(Object key, Object block) {
    return Atom.aes_ecba_de(key, block);
  }

}