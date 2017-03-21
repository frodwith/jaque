package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.truffle.Context;

@NodeField(name="context", type=Context.class)
public abstract class EscapeNode extends BinaryFormulaNode {
  @Specialization
  public Object escape(Object gate, Object sample) {
    printStub(gate, sample);
    throw new Bail();
  }
  
  @TruffleBoundary
  protected void printStub(Object gate, Object sample) {
    System.err.println("TODO - ESCAPE UNIMPLEMENTED. gate: " + gate + ", sample: " + sample);
  }
}
