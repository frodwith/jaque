package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class JamNode extends GateNode {

  @Specialization
  protected Object jam(Object noun) {
    return Atom.jam(noun);
  }

}