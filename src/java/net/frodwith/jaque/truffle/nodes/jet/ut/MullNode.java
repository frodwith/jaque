package net.frodwith.jaque.truffle.nodes.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;

public abstract class MullNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.orBail(core.tail),
         van = Cell.orBail(pay.tail),
         sam = Cell.orBail(pay.head),
         sar = Cell.orBail(sam.tail);
         
    Object sut = Cell.orBail(van.tail).head,
           gol = sam.head,
           dox = sar.head,
           gen = sar.tail;
    
    return new Cell(tip("mull", van), new Cell(sut, new Cell(gol, new Cell(dox, gen))));
  }
}
