package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;

public abstract class SameOpNode extends BlocNode {
  public abstract Object executeSame(VirtualFrame frame, Object a, Object b);

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
  
  @Fallback
  protected long same(Object a, Object b) {
    return Atom.NO;
  }
}
