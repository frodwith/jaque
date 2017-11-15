package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.UnaryOpNode;

public abstract class TripNode extends UnaryOpNode {
  @Specialization
  protected Object trip(Object atom) {
    return Atom.trip(atom);
  }
}