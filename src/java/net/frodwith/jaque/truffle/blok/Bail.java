package net.frodwith.jaque.truffle.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BailNode;
import net.frodwith.jaque.truffle.bloc.BlocNode;

public final class Bail extends Op {
  @Override
  public BlocNode toNode(Context context) {
    return new BailNode();
  }
}