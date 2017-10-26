package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;

@NodeField(name="expectedPayload", type=Object.class)
public abstract class StaticFineNode extends FineOpNode {
  public abstract Object getExpectedPayload();
  
  @Specialization
  public boolean fine(Cell core) {
    return Noun.equals(core.tail, getExpectedPayload());
  }
}
