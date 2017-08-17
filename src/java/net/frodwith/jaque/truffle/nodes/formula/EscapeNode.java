package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.truffle.Context;

@NodeField(name="context", type=Context.class)
public abstract class EscapeNode extends BinaryFormulaNode {
  @Child protected KickNode kick;
  private static final Cell kickFormula = new Qual(9L, 2L, 0L, 1L).toCell();
  
  protected abstract Context getContext();

  @Specialization
  public Object escape(VirtualFrame frame, Object ref, Object gof) {
    if ( null == kick ) {
      this.kick = (KickNode) getContext().parseCell(kickFormula, false);
    }
    Context c = getContext();
    Object old = getSubject(frame),
           pro;
    Cell gat = Cell.expect(c.levels.peek().escapeGate),
         pay = Cell.expect(gat.tail),
         yap = new Cell(new Cell(ref, gof), pay.tail),
         mut = new Cell(gat.head, yap);
    setSubject(frame, mut);
    pro = kick.executeGeneric(frame);
    // pro is a (unit (unit)), there is mushing to do,
    // it's possible we might be throwing some sort of Block exception here
    // but this is also not reached by our boot sequence yet.
    throw new RuntimeException("Still not implemented");
    /*
    setSubject(frame, old);
    return pro;
    */
  }

}
