package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.List;

public abstract class CanNode extends PairGateNode {

  @Specialization
  protected Object can(long a, Object b) {
    return Atom.can(Atom.bloqOrBail(a), new List(b));
  }

}