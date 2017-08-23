package net.frodwith.jaque.truffle.nodes.formula.hint;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.Context.Road;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

public final class StackHintNode extends DynamicHintFormula {
  private final Context context;
  private final Object zep;
  
  public StackHintNode(Context context, Object zep, FormulaNode hint, FormulaNode next) {
    super(hint, next);
    this.zep = zep;
    this.context = context;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Cell item = new Cell(zep, hint.executeGeneric(frame));
    context.stackPush(item);
    Object product = next.executeGeneric(frame);
    context.stackPop();
    return product;
  }
}
