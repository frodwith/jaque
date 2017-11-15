package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.jet.TernaryOpNode;

public abstract class CutNode extends TernaryOpNode {
  @Specialization
  protected Object cut(long a, Cell bc, Object d) {
    return Atom.cut(
        Atom.bloqOrBail(a),
        Atom.orBail(bc.head),
        Atom.orBail(bc.tail),
        Atom.orBail(d));
  }
}