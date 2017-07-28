package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class AesEcbbEnNode extends AesEcbNode {

  @Specialization
  protected Object en(Object key, Object block) {
    return Atom.aes_ecbb_en(key, block);
  }

}