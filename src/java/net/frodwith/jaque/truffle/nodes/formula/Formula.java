package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.JaqueNode;

public abstract class Formula extends JaqueNode {
  public abstract Object doSafe(VirtualFrame frame);
  public abstract Object executeGeneric(VirtualFrame frame);

  public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
    return TypesGen.expectLong(executeSafe(frame));
  }

  public int[] executeIntArray(VirtualFrame frame) throws UnexpectedResultException {
    return TypesGen.expectIntArray(executeSafe(frame));
  }
  
  public Cell executeCell(VirtualFrame frame) throws UnexpectedResultException {
    return TypesGen.expectCell(executeSafe(frame));
  }
}
