package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;

public class PushPlaceNode extends OpNode {
  private final Context context;
  private final Object kind;

  public PushPlaceNode(Context context, Object kind) {
    this.context = context;
    this.kind = kind;
  }

  @Override
  public void execute(VirtualFrame frame) {
    context.stackPush(new Cell(kind, getStack(frame).pop()));
  }

}
