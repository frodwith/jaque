package net.frodwith.jaque.truffle.bloc;

import java.util.Deque;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;

public class FinishMemoNode extends OpNode {
  private final Context context;
  private final Cell formula;
  
  public FinishMemoNode(Context context, Cell formula) {
    this.context = context;
    this.formula = formula;
  }

  @Override
  public void execute(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    Object product = s.pop();
    Object subject = s.pop();
    Cell key = new Cell(formula, subject);
    context.saveMemo(key, product);
    s.push(product);
  }

}
