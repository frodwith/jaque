package net.frodwith.jaque.truffle.blok;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.FragNode;

public final class Frag extends Op {
  public Axis axis;
  
  public Frag(Axis axis) {
    this.axis = axis;
  }

  @Override
  public BlocNode toNode(Context context) {
    return new FragNode(axis);
  }
  @Override
  public String toString() {
    return "<Frag " + Atom.toString(axis.atom) + ">";
  }
}