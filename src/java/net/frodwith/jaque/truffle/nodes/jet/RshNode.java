package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class RshNode extends TrelGateNode {

  @Specialization
  protected Object rsh(long a, long b, Object c) {
    return Atom.rsh(Atom.bloqOrBail(a), Atom.intOrBail(b), Atom.orBail(c));
  }

}