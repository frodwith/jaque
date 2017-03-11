package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.Specialization;
import net.frodwith.jaque.data.Cell;

public abstract class DeepNode extends UnaryFormula {
  @Specialization
  protected long deep(Cell c) {
    return 0L;
  }

  @Specialization
  protected long deep(Object o) {
    return 1L;
  }
}