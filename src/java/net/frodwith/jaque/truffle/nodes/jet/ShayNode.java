package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class ShayNode extends PairGateNode {

  @Specialization
  protected Object shay(Object len, Object atom) {
    return Atom.shay(len, atom);
  }

}