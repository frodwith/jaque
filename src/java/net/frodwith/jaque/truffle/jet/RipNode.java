package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class RipNode extends PairGateNode {

  @Specialization
  protected Object rip(long a, Object b) {
    return Atom.rip(Atom.bloqOrBail(a), Atom.orBail(b));
  }

}