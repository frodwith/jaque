package net.frodwith.jaque.truffle.nodes.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.FragmentationException;

public abstract class MintNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell core) {
    try {
      Cell pay = Cell.orBail(core.tail),
           van = Cell.orBail(pay.tail),
           sam = Cell.orBail(pay.head);
           
      Object sut = Cell.orBail(van.tail).head,
             vrf = vanVrf.executeFragment(van),
             gol = sam.head,
             gen = sam.tail;
      
      return new Cell(Atom.mote("mint"), new Cell(vrf, new Cell(sut, new Cell(gol, gen))));
    }
    catch (FragmentationException e) {
      throw new Bail();
    }
  }
}
