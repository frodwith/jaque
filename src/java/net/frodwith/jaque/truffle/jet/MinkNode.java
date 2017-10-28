package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;

public abstract class MinkNode extends PairGateNode {
  
  @Specialization
  public Object mink(VirtualFrame frame, Cell busfol, Cell gul) {
    Object subject  = busfol.head;
    Cell   formula  = Cell.orBail(busfol.tail);
    Context context = getContext();

    return context.softRun(gul, () -> context.nock(subject, formula));
  }

}
