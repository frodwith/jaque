package net.frodwith.jaque.truffle.nodes.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;

public abstract class FindNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.expect(core.tail),
         van = Cell.expect(pay.tail),
         sam = Cell.expect(pay.head);
         
    Object sut = Cell.expect(van.tail).head,
           way = sam.head,
           hyp = sam.tail;
    
    return new Cell(tip("find", van), new Cell(sut, new Cell(way, hyp)));
  }
}
