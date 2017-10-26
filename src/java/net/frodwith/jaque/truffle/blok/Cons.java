package net.frodwith.jaque.truffle.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.ConsNode;

public final class Cons extends Op {
  @Override
  public BlocNode toNode(Context context) {
    return new ConsNode();
  }
}