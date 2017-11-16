package net.frodwith.jaque.truffle.bloc;

import java.util.Deque;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;

public class StartMemoNode extends FlowNode {
  public final CallTarget compute;
  public final Cell formula;
  public final Context context;
  
  public StartMemoNode(Context context, Cell formula, CallTarget compute) {
    this.context = context;
    this.formula = formula;
    this.compute = compute;
  }

  @Override
  public Continuation execute(VirtualFrame frame) {
    Deque<Object> s = getStack(frame);
    Cell key = new Cell(formula, s.peek());
    Object pro = context.getMemo(key);
    if ( null == pro ) {
      s.push(s.peek());
      return Continuation.call(compute, after);
    }
    else {
      s.push(pro);
      return Continuation.jump(after);
    }
  }

}
