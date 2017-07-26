package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class PegNode extends PairGateNode {

  @Specialization
  protected Object peg(Object a, Object b) {
    return Atom.peg(Atom.expect(a), Atom.expect(b));
  }

}