package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public final class QuoteCellNode extends OpNode {
  private final Cell value;
  
  public QuoteCellNode(Cell value) {
    this.value = value;
  }

  @Override
  public void execute(VirtualFrame frame) {
    getStack(frame).push(value);
  }

}
