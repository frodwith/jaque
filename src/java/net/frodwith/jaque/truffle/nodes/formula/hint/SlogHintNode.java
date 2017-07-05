package net.frodwith.jaque.truffle.nodes.formula.hint;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

public final class SlogHintNode extends DynamicHintFormula {
  
  public SlogHintNode(FormulaNode hint, FormulaNode next) {
    super(hint, next);
  }
  
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    try {
      Cell slog = TypesGen.expectCell(hint.executeGeneric(frame));
      Cell tank = TypesGen.expectCell(slog.tail);
      int pri  = Atom.expectInt(slog.head);
      StringBuilder b = new StringBuilder();
      
      if ( pri > 0 ) {
        for ( int i = 0; i < pri; ++i ) {
          b.append('>');
        }
        b.append(' ');
      }

      if ( Atom.LEAF.equals(tank.head) ) {
        for ( Object o : new List(tank.tail) ) {
          b.append((char) Atom.expectInt(o));
        }
        System.out.println(b);
      }
      else {
        System.err.println("too dumb to print this tank");
        System.err.println(tank);
      }
    }
    catch ( UnexpectedResultException e ) {
    }
    catch ( Bail e ) {
    }
    return next.executeGeneric(frame);
  }
}
