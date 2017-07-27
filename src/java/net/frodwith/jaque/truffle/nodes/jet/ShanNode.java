package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class ShanNode extends GateNode {

  @Specialization
  protected Object shan(Object atom) {
    return Atom.shan(atom);
  }

}