package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class AesEcbaEnNode extends AesEcbNode {

  @Specialization
  protected Object en(Object key, Object block) {
    return Atom.aes_ecba_en(key, block);
  }

}