package net.frodwith.jaque.truffle.nodes.formula.hint;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

public final class StackHintNode extends DynamicHintFormula {
  private final Stack<Object> stack;
  private final Object zep;
  
  public StackHintNode(Stack<Object> stack, Object zep, FormulaNode hint, FormulaNode next) {
    super(hint, next);
    this.stack = stack;
    this.zep = zep;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object item = hint.executeGeneric(frame);
    stack.push(new Cell(zep, item));
    Object product = next.executeGeneric(frame);
    stack.pop();
    return product;
  }
}
