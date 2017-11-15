package net.frodwith.jaque.truffle.jet.ops.crypt;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.TernaryOpNode;

public abstract class EdVeriNode extends TernaryOpNode {
  @Specialization
  protected Object veri(Object s, Object m, Object pk) {
    return Atom.edVeri(s, m, pk);
  }
}