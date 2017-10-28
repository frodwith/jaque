package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class DorNode extends PairGateNode {

  @Specialization
  protected long dor(Object a, Object b) {
    return Atom.dor(a, b);
  }

}