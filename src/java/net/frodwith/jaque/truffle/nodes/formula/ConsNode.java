package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.profiles.ValueProfile;

import net.frodwith.jaque.data.Cell;

public abstract class ConsNode extends BinaryFormulaNode {
  @Specialization
  public Cell cons(Object head, Object tail) {
    return new Cell(head, tail);
  }
}
