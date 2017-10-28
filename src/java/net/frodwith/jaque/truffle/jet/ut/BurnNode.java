package net.frodwith.jaque.truffle.jet.ut;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;

public abstract class BurnNode extends PartialMemoNode {
  @Specialization
  public Cell key(Cell van) {
    Object sut = Cell.orBail(van.tail).head;
    return new Cell(tip("burn", van), sut);
  }
}
