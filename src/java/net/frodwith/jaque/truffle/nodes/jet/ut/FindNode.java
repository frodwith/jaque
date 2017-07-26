package net.frodwith.jaque.truffle.nodes.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;

public abstract class FindNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    Cell pay = Cell.expect(core.tail),
         van = Cell.expect(pay.tail),
         sam = Cell.expect(pay.head);
         
    Object sut = Cell.expect(van.tail).head,
           way = sam.head,
           hyp = sam.tail;
    
    Noun.println(hyp);
    
    return new Cell(tip("find", van), new Cell(sut, new Cell(way, hyp)));
  }
}
