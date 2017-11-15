package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.UtNode;

public final class FindNode extends UtNode {

  public FindNode(Context context, CallTarget fallback) {
    super(context, fallback);
  }

  public Cell getKey(Cell core) throws UnexpectedResultException {
    Cell pay = Cell.expect(core.tail),
         van = Cell.expect(pay.tail),
         sam = Cell.expect(pay.head);
         
    Object sut = Cell.expect(van.tail).head,
           way = sam.head,
           hyp = sam.tail;
    
    return new Cell(tip("find", van), new Cell(sut, new Cell(way, hyp)));
  }
}