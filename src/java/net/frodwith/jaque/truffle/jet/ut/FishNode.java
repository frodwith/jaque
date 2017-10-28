package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;

public abstract class FishNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.orBail(core.tail),
         van = Cell.orBail(pay.tail);
         
    Object sut = Cell.orBail(van.tail).head,
           axe = pay.head;
    
    return new Cell(tip("fish", van), new Cell(sut, axe));
  }
}
