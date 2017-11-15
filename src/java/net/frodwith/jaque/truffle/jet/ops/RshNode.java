package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.TernaryOpNode;

public abstract class RshNode extends TernaryOpNode {
  @Specialization
  protected Object rsh(long a, long b, Object c) {
    return Atom.rsh(Atom.bloqOrBail(a), Atom.intOrBail(b), Atom.orBail(c));
  }
}