package net.frodwith.jaque.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;

public abstract class Op {
  public boolean tailOnly() {
    return false;
  }
  public abstract BlocNode toNode(Context context);

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}