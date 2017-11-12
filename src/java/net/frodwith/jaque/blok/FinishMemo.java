package net.frodwith.jaque.blok;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.FinishMemoNode;

public class FinishMemo extends Op {
  public Cell key;
  
  public FinishMemo(Cell key) {
    this.key = key;
  }

  @Override
  public BlocNode toNode(Context context) {
    return new FinishMemoNode(context, key);
  }

}
