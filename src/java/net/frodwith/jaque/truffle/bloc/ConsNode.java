package net.frodwith.jaque.truffle.bloc;

import java.util.Deque;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public final class ConsNode extends OpNode {

  @Override
  public void execute(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    Object a = s.pop();
    Object b = s.pop();
    s.push(new Cell(a, b));
  }

}
