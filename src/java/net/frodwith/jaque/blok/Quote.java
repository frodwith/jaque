package net.frodwith.jaque.blok;

import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.bloc.BlocNode;
import net.frodwith.jaque.truffle.bloc.QuoteCellNode;
import net.frodwith.jaque.truffle.bloc.QuoteLongNode;
import net.frodwith.jaque.truffle.bloc.QuoteWordsNode;

public final class Quote extends Op {
  public final Object value;
  public Quote(Object value) {
    this.value = value;
  }
  @Override
  public BlocNode toNode(Context context) {
    if ( TypesGen.isCell(value) ) {
      return new QuoteCellNode(TypesGen.asCell(value));
    }
    else if ( TypesGen.isLong(value) ) {
      return new QuoteLongNode(TypesGen.asLong(value));
    }
    else {
      return new QuoteWordsNode(TypesGen.asIntArray(value));
    }
  }
  @Override
  public String toString() {
    return "<Quote " + Noun.toString(value) + ">";
  }
}