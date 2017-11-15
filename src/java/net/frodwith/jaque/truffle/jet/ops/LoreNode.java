package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.UnaryOpNode;

public abstract class LoreNode extends UnaryOpNode {
  @Specialization
  protected Object lore(Object atom) {
    return Atom.lore(atom);
  }
}