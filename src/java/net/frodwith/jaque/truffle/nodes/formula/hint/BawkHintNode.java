package net.frodwith.jaque.truffle.nodes.formula.hint;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.data.Tank;
import net.frodwith.jaque.data.Tape;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

public final class BawkHintNode extends DynamicHintFormula {
  
  public BawkHintNode(FormulaNode hint, FormulaNode next) {
    super(hint, next);
  }
  
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object bawk = hint.executeGeneric(frame);
    System.out.print("BAWK! ");
    System.out.flush();
    Noun.println(bawk);
    return next.executeGeneric(frame);
  }
}
