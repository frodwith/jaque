package net.frodwith.jaque.truffle.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.FastNode;

public final class Fast extends Op {
  @Override
  public BlocNode toNode(Context context) {
    return new FastNode(context);
  }
}