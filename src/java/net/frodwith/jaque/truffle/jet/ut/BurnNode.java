package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.UtNode;

public final class BurnNode extends UtNode {

  public BurnNode(Context context, CallTarget fallback) {
    super(context, fallback);
  }

  public Cell getKey(Cell core) throws UnexpectedResultException {
    Object sut = Cell.expect(core.tail).head;
    return new Cell(tip("burn", core), sut);
  }
}
