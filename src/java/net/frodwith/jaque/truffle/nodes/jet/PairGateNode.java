package net.frodwith.jaque.truffle.nodes.jet;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.TypesGen;

public abstract class PairGateNode extends GateNode {
  protected abstract Object executePair(Object a, Object b);
  
  @Override
  public Object doGate(Object subject) {
    if ( !TypesGen.isCell(subject) ) {
      throw new Bail();
    }
    Cell c = TypesGen.asCell(subject);
    return executePair(c.head, c.tail);
  }
}
