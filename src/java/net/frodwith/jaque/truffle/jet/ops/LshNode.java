package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.TernaryOpNode;

public abstract class LshNode extends TernaryOpNode {
  @Specialization
  protected Object lsh(long a, long b, Object c) {
    return Atom.lsh(Atom.bloqOrBail(a), Atom.intOrBail(b), Atom.orBail(c));
  }
}