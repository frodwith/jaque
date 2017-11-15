package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.TernaryOpNode;

public abstract class MukNode extends TernaryOpNode {
  @Specialization
  protected long muk(long seed, long length, Object key) {
    return Atom.muk(Atom.unsignedIntOrBail(seed),
        Atom.unsignedIntOrBail(length),
        Atom.orBail(key));
  }
}