package net.frodwith.jaque.truffle.jet;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.bloc.Continuation;
import net.frodwith.jaque.truffle.bloc.RootNode;

public class SaveUtMemoRootNode extends RootNode {
  private final Context context;
  
  public SaveUtMemoRootNode(Context context) {
    this.context = context;
  }

  @Override
  public Continuation execute(VirtualFrame frame) {
    @SuppressWarnings("unchecked")
    Stack<Object> s = (Stack<Object>) frame.getArguments()[0];
    Object product = s.pop();
    Cell   key     = Cell.orBail(s.pop());
    context.saveMemo(key, product);
    s.push(product);
    return Continuation.ret();
  }
  
}
