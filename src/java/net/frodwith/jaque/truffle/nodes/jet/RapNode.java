package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;

public abstract class RapNode extends PairGateNode {

  @Specialization
  protected Object rap(long a, Cell b) {
    return Atom.rap(Atom.expectBloq(a), new List(b));
  }

}