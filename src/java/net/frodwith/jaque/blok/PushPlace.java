package net.frodwith.jaque.blok;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.PushPlaceNode;

public final class PushPlace extends Op {
  public final Object kind;
  public PushPlace(Object kind) {
    this.kind = kind;
  }
  @Override
  public BlocNode toNode(Context context) {
    return new PushPlaceNode(context, kind);
  }
  @Override
  public String toString() {
    return "<PushPlace " + Atom.cordToString(kind) + ">";
  }
}