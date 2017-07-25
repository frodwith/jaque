package net.frodwith.jaque.truffle.nodes.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;

public abstract class MullNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.expect(core.tail),
         van = Cell.expect(pay.tail),
         sam = Cell.expect(pay.head),
         sar = Cell.expect(sam.tail);
         
    Object sut = Cell.expect(van.tail).head,
           gol = sam.head,
           dox = sar.head,
           gen = sar.tail;
    
    return new Cell(tip("mull", van), new Cell(sut, new Cell(gol, new Cell(dox, gen))));
  }
}
