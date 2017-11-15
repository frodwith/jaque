package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.UnaryOpNode;

public abstract class CueNode extends UnaryOpNode {
  @Specialization
  protected Object cue(Object atom) {
    return Atom.cue(atom);
  }
}