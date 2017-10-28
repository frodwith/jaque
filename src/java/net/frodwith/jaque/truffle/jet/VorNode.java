package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class VorNode extends PairGateNode {

  @Specialization
  protected long vor(Object a, Object b) {
    return Atom.vor(a, b);
  }

}