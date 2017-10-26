package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

public class BlockRootNode extends RootNode {
  private BlockNode program;
  
  public BlockRootNode(BlockNode program) {
    this.program = program;
  }

  public Continuation execute(VirtualFrame frame) {
    frame.setObject(BlocNode.STACK, frame.getArguments()[0]);
    return program.execute(frame);
  }
}
