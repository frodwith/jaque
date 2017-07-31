package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class EdVeriNode extends TrelGateNode {

  @Specialization
  protected Object veri(Object s, Object m, Object pk) {
    return Atom.edVeri(s, m, pk);
  }

}