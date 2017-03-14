package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.JaqueNode;

public abstract class JetNode extends JaqueNode {
  public abstract Object executeJet(Cell core);
  
  public long executeLong(Cell core) throws UnexpectedResultException {
    return TypesGen.expectLong(executeJet(core));
  }

  public int[] executeIntArray(Cell core) throws UnexpectedResultException {
    return TypesGen.expectIntArray(executeJet(core));
  }

  public Cell executeCell(Cell core) throws UnexpectedResultException {
    return TypesGen.expectCell(executeJet(core));
  }
  
}
