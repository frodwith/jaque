package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;

public abstract class FondNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.orBail(core.tail),
         van = Cell.orBail(pay.tail),
         sam = Cell.orBail(pay.head);
         
    Object sut = Cell.orBail(van.tail).head,
           way = sam.head,
           hyp = sam.tail;
    
    return new Cell(tip("fond", van), new Cell(sut, new Cell(way, hyp)));
  }
}
