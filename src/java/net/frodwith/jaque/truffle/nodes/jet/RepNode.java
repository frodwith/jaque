package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;

public abstract class RepNode extends PairGateNode {

  @Specialization
  protected Object rep(long a, Cell b) {
    return Atom.rep(Atom.expectBloq(a), new List(b));
  }

}