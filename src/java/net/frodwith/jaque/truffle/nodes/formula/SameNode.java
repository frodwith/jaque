package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.Specialization;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;

public abstract class SameNode extends BinaryFormulaNode {
  @Specialization
  protected long same(long a, long b) {
    return Atom.equals(a, b) ? Atom.YES : Atom.NO;
  }

  @Specialization
  protected long same(int[] a, int[] b) {
    return Atom.equals(a, b) ? Atom.YES : Atom.NO;
  }
  
  @Specialization
  protected long same(Cell a, Cell b) {
    return Cell.equals(a, b) ? Atom.YES : Atom.NO;
  }
  
  @Specialization
  protected long same(Object a, Object b) {
    return Atom.NO;
  }
}