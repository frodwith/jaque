package net.frodwith.jaque.blok;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.StartMemoNode;

public class StartMemo extends Op {
  public Cell key;
  public Block body;
  
  public StartMemo(Cell key, Block body) {
    this.key = key;
    this.body = body;
  }
  
  @Override
  public boolean tailOnly() {
    return true;
  }

  @Override
  public BlocNode toNode(Context context) {
    return new StartMemoNode(context, key, body.toTarget(context));
  }

}
