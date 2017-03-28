package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.TypesGen;

public abstract class TrelGateNode extends GateNode {
  protected abstract Object executeTrel(Object a, Object b, Object c);
  
  @Override
  public Object doGate(Object subject) {
    try {
      Cell trel = TypesGen.expectCell(subject),
           pair = TypesGen.expectCell(trel.tail);
      return executeTrel(trel.head, pair.head, pair.tail);
    }
    catch (UnexpectedResultException e) {
      throw new Bail();
    }
  }
}
