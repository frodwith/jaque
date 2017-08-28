package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.BlockException;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TypesGen;

@NodeField(name="context", type=Context.class)
public abstract class EscapeNode extends BinaryFormulaNode {
  protected abstract Context getContext();

  @Specialization
  public Object escape(VirtualFrame frame, Object ref, Object gof) {
    Context c = getContext();
    Object pro = c.softEscape(ref, gof);
    
    if ( !Noun.isCell(pro) ) {
      throw new BlockException(gof);
    }
    else {
      Cell cro = TypesGen.asCell(pro);
      if ( !Noun.isCell(cro.tail) ) {
        Cell item = new Cell(Atom.mote("hunk"), c.kernel("mush", gof));
        c.stackPush(item);
        throw new Bail();
      }
      else {
        return TypesGen.asCell(cro.tail).tail;
      }
    }
  }

}
