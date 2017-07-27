package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class ShalNode extends PairGateNode {

  @Specialization
  protected Object shal(Object len, Object atom) {
    return Atom.shal(len, atom);
  }

}