package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.truffle.Context;

public class PopPlaceNode extends OpNode {
  private final Context context;

  public PopPlaceNode(Context context) {
    this.context = context;
  }

  @Override
  public void execute(VirtualFrame frame) {
    context.stackPop();
  }

}
