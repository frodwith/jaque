package net.frodwith.jaque.truffle.nodes.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;

public abstract class CropNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.expect(core.tail),
         van = Cell.expect(pay.tail);
         
    Object sut = Cell.expect(van.tail).head,
           ref = pay.head;
    
    return new Cell(tip("crop", van), new Cell(sut, ref));
  }
}
