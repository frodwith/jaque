package net.frodwith.jaque.truffle.nodes.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;

public abstract class PeekNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.expect(core.tail),
         van = Cell.expect(pay.tail),
         sam = Cell.expect(pay.head);

    Object sut = Cell.expect(van.tail).head,
           way = sam.head,
           axe = sam.tail;
    
    return new Cell(tip("peek", van), new Cell(sut, new Cell(way, axe)));
  }
}
