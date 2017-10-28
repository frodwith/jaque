package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;

public abstract class MintNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    try {
      Cell pay = Cell.expect(core.tail),
           van = Cell.expect(pay.tail),
           sam = Cell.expect(pay.head);
           
      Object sut = Cell.expect(van.tail).head,
             vrf = vanVrf.fragment(van),
             gol = sam.head,
             gen = sam.tail;
      
      return new Cell(Atom.mote("mint"), new Cell(vrf, new Cell(sut, new Cell(gol, gen))));
    }
    catch ( UnexpectedResultException e) {
      throw new Bail();
    }
  }
}
