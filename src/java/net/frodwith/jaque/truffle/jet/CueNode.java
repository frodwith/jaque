package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class CueNode extends GateNode {

  @Specialization
  protected Object cue(Object atom) {
    return Atom.cue(atom);
  }

}