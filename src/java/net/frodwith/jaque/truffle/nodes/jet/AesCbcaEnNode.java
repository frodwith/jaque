package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;
import net.frodwith.jaque.data.Atom;

public abstract class AesCbcaEnNode extends AesCbcNode {

  @Specialization
  protected Object en(Object key, Object iv, Object msg) {
    return Atom.aes_cbca_en(key, iv, msg);
  }

}