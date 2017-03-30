package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class MetNode extends PairGateNode {

  @Specialization
  protected long met(long a, Object b) {
    return Atom.met(Atom.expectBloq(a), Atom.expect(b));
  }

}