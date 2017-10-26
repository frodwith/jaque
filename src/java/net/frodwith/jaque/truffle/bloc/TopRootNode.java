package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;
import java.util.Optional;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;

public class TopRootNode extends RootNode {
  @Child private BlockNode program;
  @Child private IndirectCallNode callNode;
  
  public TopRootNode(BlockNode program) {
    this.program = program;
    this.callNode = IndirectCallNode.create();
  }

  @Override
  public Object execute(VirtualFrame frame) {
    Stack<Object> data = new Stack<Object>();
    Stack<Continuation> cont = new Stack<Continuation>();
    data.push(frame.getArguments()[0]);
    Object[] args = new Object[] { data };
    frame.setObject(BlocNode.STACK, data);
    cont.push(program.execute(frame));
    while ( !cont.empty() ) {
      Continuation k = cont.pop();
      if ( null != k.target ) {
        if ( null != k.after ) {
          cont.push(Continuation.jump(k.after));
        }
        cont.push( (Continuation) callNode.call(frame, k.target, args) );
      }
    }
    return data.pop();
  }

}
