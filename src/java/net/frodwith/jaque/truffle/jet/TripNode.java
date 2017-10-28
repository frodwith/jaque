package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class TripNode extends GateNode {

  @Specialization
  protected Object trip(Object atom) {
    return Atom.trip(atom);
  }

}