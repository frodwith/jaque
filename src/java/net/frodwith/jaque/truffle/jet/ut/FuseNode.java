package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.UtNode;

public final class FuseNode extends UtNode {

  public FuseNode(Context context, CallTarget fallback) {
    super(context, fallback);
  }

  public Cell getKey(Cell core) throws UnexpectedResultException {
    Cell pay = Cell.expect(core.tail),
         van = Cell.expect(pay.tail);
         
    Object sut = Cell.expect(van.tail).head,
           ref = pay.head;
    
    return new Cell(tip("fuse", van), new Cell(sut, ref));
  }
}