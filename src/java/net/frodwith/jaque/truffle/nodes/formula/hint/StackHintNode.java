package net.frodwith.jaque.truffle.nodes.formula.hint;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

public final class StackHintNode extends DynamicHintFormula {
  private final Stack<Object> stack;
  
  public StackHintNode(Stack<Object> stack, FormulaNode hint, FormulaNode next) {
    super(hint, next);
    this.stack = stack;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object item = hint.executeGeneric(frame);
    stack.push(item);
    Object product = next.executeGeneric(frame);
    stack.pop();
    return product;
  }
}
