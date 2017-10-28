package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class EdSharNode extends PairGateNode {

  @Specialization
  protected Object shar(Object pub, Object sek) {
    return Atom.edShar(pub, sek);
  }

}