package net.frodwith.jaque.truffle.nodes.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;

public abstract class NestDextNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.expect(core.tail),
         van = Cell.expect(pay.tail),
         sam = Cell.expect(pay.head);
         
    Object sut = Cell.expect(van.tail).head,
           ref = sam.tail;
    
    return new Cell(tip("nest", van), new Cell(sut, ref));
  }
}
