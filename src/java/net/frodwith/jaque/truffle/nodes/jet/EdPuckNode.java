package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class EdPuckNode extends GateNode {

  @Specialization
  protected Object puck(Object seed) {
    return Atom.edPuck(seed);
  }

}