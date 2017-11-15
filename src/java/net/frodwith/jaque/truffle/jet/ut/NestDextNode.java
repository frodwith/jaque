package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.UtNode;

public final class NestDextNode extends UtNode {

  public NestDextNode(Context context, CallTarget fallback) {
    super(context, fallback);
  }

  public Cell getKey(Cell core) throws UnexpectedResultException {
    Cell pay = Cell.expect(core.tail),
         gat = Cell.expect(pay.tail),
         gap = Cell.expect(gat.tail),
         van = Cell.expect(gap.tail),
         gas = Cell.expect(gap.head);
         
    Object sut = Cell.expect(van.tail).head,
           ref = gas.tail;
    
    return new Cell(tip("nest", van), new Cell(sut, ref));
  }
}