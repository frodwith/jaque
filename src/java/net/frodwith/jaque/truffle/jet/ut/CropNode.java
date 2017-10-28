package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;

public abstract class CropNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.orBail(core.tail),
         van = Cell.orBail(pay.tail);
         
    Object sut = Cell.orBail(van.tail).head,
           ref = pay.head;
    
    return new Cell(tip("crop", van), new Cell(sut, ref));
  }
}
