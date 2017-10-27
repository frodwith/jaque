package net.frodwith.jaque.truffle.blok;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.CallNode;

public final class Call extends Op {
  public final Axis axis;
  public Call(Axis axis) {
    this.axis = axis;
  }
  @Override
  public boolean tailOnly() {
    return true;
  }
  @Override
  public BlocNode toNode(Context context) {
    return new CallNode(context, axis);
  }
  @Override
  public String toString() {
    return "<Call " + Atom.toString(axis.atom) + ">";
  }
}