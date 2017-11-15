package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class RapNode extends BinaryOpNode {
  @Specialization
  protected Object rap(long a, Object b) {
    return Atom.rap(Atom.bloqOrBail(a), new List(b));
  }
}