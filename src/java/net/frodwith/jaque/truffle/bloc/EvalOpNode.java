package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;

public abstract class EvalOpNode extends BlocNode {
  public abstract CallTarget executeTarget(VirtualFrame frame, Context context, Object formula);
  
  @Specialization
  public CallTarget target(Context context, Cell formula) {
    return context.evalByCell(formula);
  }
}
