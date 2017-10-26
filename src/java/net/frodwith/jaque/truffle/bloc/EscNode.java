package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.BlockException;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TypesGen;

public final class EscNode extends OpNode {
  private final Context context;
  private static final Object HUNK = Atom.mote("hunk");
  
  public EscNode(Context context) {
    this.context = context;
  }
  
  // It might make sense to view Escape as a kind of control flow. We've chosen
  // not to regard it that way for simplicity, but consequentially escapes do in
  // fact consume java stack space. This should not be a problem in practice.
  @Override
  public void execute(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    Object ref = s.pop();
    Object gof = s.pop();
    Object pro = context.softEscape(ref, gof);
    if ( !Noun.isCell(pro) ) {
      throw new BlockException(gof);
    }
    else {
      Cell cro = TypesGen.asCell(pro);
      if ( !Noun.isCell(cro.tail) ) {
        Cell item = new Cell(HUNK, context.kernel("mush", gof));
        context.stackPush(item);
        throw new Bail();
      }
      else {
        s.push(TypesGen.asCell(cro.tail).tail);
      }
    }
  }

}
