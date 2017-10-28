package net.frodwith.jaque.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.MemoNode;

public final class Memo extends Op {
  public final Block body;
  public Memo(Block body) {
    this.body = body;
  }
  @Override
  public BlocNode toNode(Context context) {
    return new MemoNode(body.cps(context));
  }
}