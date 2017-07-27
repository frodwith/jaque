package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class AescEnNode extends PairGateNode {

  @Specialization
  protected Object en(Object key, Object txt) {
    return Atom.aescEn(key, txt);
  }

}