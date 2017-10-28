package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class LshNode extends TrelGateNode {

  @Specialization
  protected Object lsh(long a, long b, Object c) {
    return Atom.lsh(Atom.bloqOrBail(a), Atom.intOrBail(b), Atom.orBail(c));
  }

}