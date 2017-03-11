package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Bail;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.JaqueNode;

public abstract class Formula extends JaqueNode {
  public abstract Object executeSubject(VirtualFrame frame, Object subject);
  public abstract Object executeSafe(VirtualFrame frame, Object subject);
  
  public Object executeGeneric(VirtualFrame frame) {
    return executeSubject(frame, getSubject(frame));
  }

  public long executeLong(VirtualFrame frame, Object subject) {
    try {
      return TypesGen.expectLong(executeSafe(frame, subject));
    }
    catch (UnexpectedResultException e) {
      throw new Bail();
    }
  }

  public int[] executeIntArray(VirtualFrame frame, Object subject) {
    try {
      return TypesGen.expectIntArray(executeSafe(frame, subject));
    }
    catch (UnexpectedResultException e) {
      throw new Bail();
    }
  }

  public Cell executeCell(VirtualFrame frame, Object subject) {
    try {
      return TypesGen.expectCell(executeSafe(frame, subject));
    }
    catch (UnexpectedResultException e) {
      throw new Bail();
    }
  }
}
