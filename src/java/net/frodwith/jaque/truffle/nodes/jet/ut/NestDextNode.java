package net.frodwith.jaque.truffle.nodes.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;

public abstract class NestDextNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
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
