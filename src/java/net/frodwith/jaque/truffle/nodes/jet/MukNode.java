package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class MukNode extends TrelGateNode {

  @Specialization
  protected long muk(long seed, long length, Object key) {
    return Atom.muk(Atom.expectUnsignedInt(seed),
        Atom.expectUnsignedInt(length),
        Atom.expect(key));
  }

}