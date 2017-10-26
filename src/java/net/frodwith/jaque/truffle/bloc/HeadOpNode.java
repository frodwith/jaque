package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.dsl.Specialization;
import net.frodwith.jaque.data.Cell;

public abstract class HeadOpNode extends ReadOpNode {
  @Specialization
  public Object head(Cell c) {
    return c.head;
  }
}
