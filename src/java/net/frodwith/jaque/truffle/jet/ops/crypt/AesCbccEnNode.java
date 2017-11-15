package net.frodwith.jaque.truffle.jet.ops.crypt;

import com.oracle.truffle.api.dsl.Specialization;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.TernaryOpNode;

public abstract class AesCbccEnNode extends TernaryOpNode {
  @Specialization
  protected Object en(Object key, Object iv, Object msg) {
    return Atom.aes_cbcc_en(key, iv, msg);
  }
}