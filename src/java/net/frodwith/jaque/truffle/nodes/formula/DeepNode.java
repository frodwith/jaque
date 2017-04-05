package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;

public abstract class DeepNode extends UnaryFormulaNode {

  @Specialization
  protected long deep(Cell c) {
    return Atom.YES;
  }

  @Specialization
  protected long deep(long l) {
    return Atom.NO;
  }

  @Specialization
  protected long deep(int[] a) {
    return Atom.NO;
  }

}