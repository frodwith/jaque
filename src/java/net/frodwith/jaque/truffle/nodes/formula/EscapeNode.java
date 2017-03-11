package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.truffle.Bail;

public abstract class EscapeNode extends BinaryFormula {
  @Specialization
  public Object escape(Object gate, Object sample) {
    System.err.println("TODO - ESCAPE UNIMPLEMENTED. gate: " + gate + ", sample: " + sample);
    throw new Bail();
  }
}
