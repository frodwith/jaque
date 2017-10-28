package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.List;

public abstract class RepNode extends PairGateNode {

  @Specialization
  protected Object rep(long a, Object b) {
    return Atom.rep(Atom.bloqOrBail(a), new List(b));
  }

}