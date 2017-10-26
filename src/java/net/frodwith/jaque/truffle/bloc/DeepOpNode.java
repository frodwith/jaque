package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.nodes.JaqueNode;

public abstract class DeepOpNode extends JaqueNode {
  public abstract long executeDeep(VirtualFrame frame, Object o);
  
  @Specialization
  public long deep(Cell c) {
    return Atom.YES;
  }
  
  @Specialization
  public long deep(long l) {
    return Atom.NO;
  }
  
  @Specialization
  public long deep(int[] w) {
    return Atom.NO;
  }
}
