package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class CatNode extends TrelGateNode {

  @Specialization
  protected Object cat(long a, Object b, Object c) {
    return Atom.cat(Atom.bloqOrBail(a), Atom.orBail(b), Atom.orBail(c));
  }

}