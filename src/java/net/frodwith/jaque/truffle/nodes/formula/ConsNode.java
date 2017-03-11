package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.TypesGen;

public abstract class ConsNode extends BinaryFormula {
  @Override
  public Object executeBinary(VirtualFrame frame, Object head, Object tail) {
    return new Cell(head, tail);
  }
  
  @Override
  public Cell executeCell(VirtualFrame frame, Object subject) {
    return TypesGen.asCell(executeSubject(frame, subject));
  }
}
