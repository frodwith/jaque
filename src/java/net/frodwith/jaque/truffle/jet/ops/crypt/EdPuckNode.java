package net.frodwith.jaque.truffle.jet.ops.crypt;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.UnaryOpNode;

public abstract class EdPuckNode extends UnaryOpNode {
  @Specialization
  protected Object puck(Object seed) {
    return Atom.edPuck(seed);
  }
}