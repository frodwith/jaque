package net.frodwith.jaque.truffle.bloc;

import java.util.Deque;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;

public class SlogNode extends OpNode {
  private final Context context;

  public SlogNode(Context context) {
    this.context = context;
  }
  
  @TruffleBoundary
  private void slog(Object tank) {
    context.slog(tank);
  }

  @Override
  public void execute(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    Object raw = s.pop();
    try {
      Cell c = Cell.expect(raw);
      slog(c.tail);
    }
    catch ( UnexpectedResultException e ) {
    }
  }

}
