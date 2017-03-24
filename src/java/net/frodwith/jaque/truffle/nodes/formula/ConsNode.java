package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;

public abstract class ConsNode extends BinaryFormulaNode {

  @Specialization
  public DynamicObject cons(Object head, Object tail) {
    return Context.cons(head, tail);
  }
}
