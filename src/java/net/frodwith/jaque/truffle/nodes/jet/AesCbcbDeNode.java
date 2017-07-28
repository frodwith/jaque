package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class AesCbcbDeNode extends TrelGateNode {

  @Specialization
  protected Object de(Object key, Object iv, Object msg) {
    return Atom.aes_cbcb_de(key, iv, msg);
  }

}