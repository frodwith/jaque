package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class GorNode extends PairGateNode {

  @Specialization
  protected long gor(Object a, Object b) {
    return Atom.gor(a, b);
  }

}