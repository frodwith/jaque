package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.nodes.NockDispatchNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNodeGen;

public abstract class MinkNode extends PairGateNode {
  @Child private NockDispatchNode dispatch = null;
  
  @Specialization
  public Object mink(VirtualFrame frame, Cell busfol, Cell gul) {
    if ( dispatch == null ) {
      dispatch = NockDispatchNodeGen.create(getContext(), false);
      insert(dispatch);
    }
    Object subject = busfol.head;
    Cell   formula = Cell.expect(busfol.tail);

    return getContext().softRun(gul, () -> dispatch.executeNock(frame, subject, formula));
  }

}
