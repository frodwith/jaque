package net.frodwith.jaque.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.BumpNode;

public final class Bump extends Op {

  @Override
  public BlocNode toNode(Context context) {
    return new BumpNode();
  }

}