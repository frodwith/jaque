package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class LshNode extends TrelGateNode {

  @Specialization
  protected Object lsh(long a, long b, Object c) {
    return Atom.lsh(Atom.expectBloq(a), Atom.expectInt(b), Atom.expect(c));
  }

}