package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;

public abstract class RestNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.orBail(core.tail),
         van = Cell.orBail(pay.tail);
         
    Object sut = Cell.orBail(van.tail).head,
           leg = pay.head;
    
    return new Cell(tip("rest", van), new Cell(sut, leg));
  }
}
