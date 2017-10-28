package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class LoreNode extends GateNode {

  @Specialization
  protected Object lore(Object atom) {
    return Atom.lore(atom);
  }

}