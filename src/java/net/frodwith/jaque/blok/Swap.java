package net.frodwith.jaque.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.SwapNode;

public final class Swap extends Op {
  @Override
  public BlocNode toNode(Context context) {
    return new SwapNode();
  }
}