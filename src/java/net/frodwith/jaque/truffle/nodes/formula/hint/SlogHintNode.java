package net.frodwith.jaque.truffle.nodes.formula.hint;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.data.Tank;
import net.frodwith.jaque.data.Tape;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

public final class SlogHintNode extends DynamicHintFormula {
  
  public SlogHintNode(FormulaNode hint, FormulaNode next) {
    super(hint, next);
  }
  
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    try {
      Cell slog = Cell.expect(hint.executeGeneric(frame));
      Cell tank = Cell.expect(slog.tail);
      int  pri  = Atom.expectInt(slog.head);
      
      if ( pri > 0 ) {
        for ( int i = 0; i < pri; ++i ) {
          System.out.print('>');
        }
        System.out.print(' ');
      }
      
      for ( Object line : new List(Tank.wash(2L, 80L, tank)) ) {
        System.out.println(Tape.toString(line));
      }
    }
    catch ( Bail e ) {
    }
    return next.executeGeneric(frame);
  }
}
