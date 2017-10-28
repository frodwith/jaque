package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;

public abstract class NestDextNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.orBail(core.tail),
         gat = Cell.orBail(pay.tail),
         gap = Cell.orBail(gat.tail),
         van = Cell.orBail(gap.tail),
         gas = Cell.orBail(gap.head);
         
    Object sut = Cell.orBail(van.tail).head,
           ref = gas.tail;
    
    return new Cell(tip("nest", van), new Cell(sut, ref));
  }
  
}
