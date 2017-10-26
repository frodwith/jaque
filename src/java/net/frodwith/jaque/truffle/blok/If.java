package net.frodwith.jaque.truffle.blok;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.IfNode;

public final class If extends Op {
  public Block yes, no;

  public If(Block yes, Block no) {
    this.yes = yes;
    this.no = no;
  }
  
  @Override
  public boolean tailOnly() {
    return true;
  }

  @Override
  public BlocNode toNode(Context context) {
    return new IfNode(yes.toTarget(context), no.toTarget(context));
  }
}