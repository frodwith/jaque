package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class EndNode extends TrelGateNode {

  @Specialization
  protected Object end(long a, Object b, Object c) {
    return Atom.end(Atom.bloqOrBail(a), Atom.orBail(b), Atom.orBail(c));
  }

}