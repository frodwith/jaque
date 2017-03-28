package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;

public abstract class CanNode extends PairGateNode {

  @Specialization
  protected Object can(long a, Cell b) {
    if ( a >= 32 || a < 0 ) {
      throw new Bail();
    }
    return Atom.can((byte) a, new List(b));
  }

}