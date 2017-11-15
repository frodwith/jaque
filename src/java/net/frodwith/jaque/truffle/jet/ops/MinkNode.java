package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

@NodeField(name="context", type=Context.class)
public abstract class MinkNode extends BinaryOpNode {
  public abstract Context getContext(); 
  
  @Specialization
  protected Object mink(Cell busfol, Cell gul) {
    Object subject  = busfol.head;
    Cell   formula  = Cell.orBail(busfol.tail);
    Context context = getContext();

    return context.softRun(gul, () -> context.nock(subject, formula));
  }
}