package net.frodwith.jaque.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.PopPlaceNode;

public final class PopPlace extends Op {
  @Override
  public BlocNode toNode(Context context) {
    return new PopPlaceNode(context);
  }
}