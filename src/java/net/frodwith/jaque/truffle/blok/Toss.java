package net.frodwith.jaque.truffle.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.TossNode;

public final class Toss extends Op {
  @Override
  public BlocNode toNode(Context context) {
    return new TossNode();
  }
}