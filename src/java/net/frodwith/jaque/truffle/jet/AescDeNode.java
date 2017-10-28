package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class AescDeNode extends PairGateNode {

  @Specialization
  protected Object de(Object key, Object txt) {
    return Atom.aescDe(key, txt);
  }

}