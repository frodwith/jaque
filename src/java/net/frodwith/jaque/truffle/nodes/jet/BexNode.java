package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class BexNode extends GateNode {

  @Specialization
  protected Object bex(long atom) {
    return Atom.bex(atom);
  }

}