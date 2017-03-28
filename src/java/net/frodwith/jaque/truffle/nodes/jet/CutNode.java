package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;

public abstract class CutNode extends TrelGateNode {

  @Specialization
  protected Object cat(long a, Cell bc, Object d) {
    return Atom.cut(
        Atom.bloq(a),
        Atom.expect(bc.head),
        Atom.expect(bc.tail),
        Atom.expect(d));
  }

}