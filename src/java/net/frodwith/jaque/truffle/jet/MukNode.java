package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class MukNode extends TrelGateNode {

  @Specialization
  protected long muk(long seed, long length, Object key) {
    return Atom.muk(Atom.unsignedIntOrBail(seed),
        Atom.unsignedIntOrBail(length),
        Atom.orBail(key));
  }

}