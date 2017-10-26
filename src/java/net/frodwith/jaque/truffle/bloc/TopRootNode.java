package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;

public class TopRootNode extends RootNode {
  @Child private BlockNode program;
  @Child private IndirectCallNode callNode;

  @Override
  public Object execute(VirtualFrame frame) {
    Stack<Object> data = new Stack<Object>();
    Stack<Continuation> cont = new Stack<Continuation>();
    Object[] args = new Object[] { data };
    frame.setObject(BlocNode.STACK, data);
    cont.push(program.execute(frame));
    while ( !cont.empty() ) {
      Continuation k = cont.pop();
      if ( null != k ) {
        cont.push( (null == k.after) ? null : new Continuation(k.after, null) );
        cont.push( (Continuation) callNode.call(frame, k.target, args) );
      }
    }
    return data.pop();
  }

}
