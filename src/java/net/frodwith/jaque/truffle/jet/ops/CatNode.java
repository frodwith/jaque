package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.TernaryOpNode;

public abstract class CatNode extends TernaryOpNode {
  @Specialization
  protected Object cat(long a, Object b, Object c) {
    return Atom.cat(Atom.bloqOrBail(a), Atom.orBail(b), Atom.orBail(c));
  }
}