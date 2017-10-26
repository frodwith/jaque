package net.frodwith.jaque.truffle.nodes.formula.hint;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.data.Tank;
import net.frodwith.jaque.data.Tape;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

public final class SlogHintNode extends DynamicHintFormula {
  private final Context context;
  
  public SlogHintNode(Context context, FormulaNode hint, FormulaNode next) {
    super(hint, next);
    this.context = context;
  }
  
  @TruffleBoundary
  private void doSlog(Object tank) {
    context.slog(tank);
  }
  
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    try {
      Cell slog = Cell.orBail(hint.executeGeneric(frame));
      Cell tank = Cell.orBail(slog.tail);
      doSlog(tank);
    }
    catch ( Bail e ) {
      context.err("bad slog");
    }
    return next.executeGeneric(frame);
  }
}
