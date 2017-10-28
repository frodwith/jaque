package net.frodwith.jaque.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.EvalNode;

public final class Eval extends Op {
  @Override
  public boolean tailOnly() {
    return true;
  }
  @Override
  public BlocNode toNode(Context context) {
    return new EvalNode(context);
  }
}