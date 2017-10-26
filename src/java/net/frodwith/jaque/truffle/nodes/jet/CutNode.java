package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;

public abstract class CutNode extends TrelGateNode {

  @Specialization
  protected Object cut(long a, Cell bc, Object d) {
    return Atom.cut(
        Atom.bloqOrBail(a),
        Atom.orBail(bc.head),
        Atom.orBail(bc.tail),
        Atom.orBail(d));
  }

}