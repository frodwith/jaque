package net.frodwith.jaque.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.DupNode;

public final class Dup extends Op {
  @Override
  public BlocNode toNode(Context context) {
    return new DupNode();
  }
}