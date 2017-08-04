package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;

public abstract class CanNode extends PairGateNode {

  @Specialization
  protected Object can(long a, Object b) {
    return Atom.can(Atom.expectBloq(a), new List(b));
  }

}