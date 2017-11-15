package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.UtNode;

public final class MullNode extends UtNode {

  public MullNode(Context context, CallTarget fallback) {
    super(context, fallback);
  }

  public Cell getKey(Cell core) throws UnexpectedResultException {
    Cell pay = Cell.expect(core.tail),
         van = Cell.expect(pay.tail),
         sam = Cell.expect(pay.head),
         sar = Cell.expect(sam.tail);
         
    Object sut = Cell.expect(van.tail).head,
           gol = sam.head,
           dox = sar.head,
           gen = sar.tail;
    
    return new Cell(tip("mull", van), new Cell(sut, new Cell(gol, new Cell(dox, gen))));
  }
}