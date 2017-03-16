package net.frodwith.jaque.truffle.nodes;

import net.frodwith.jaque.truffle.NockLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public class JaqueRootNode extends RootNode {
  @Child private FunctionNode root;
  
  public JaqueRootNode(FunctionNode root) {
    super(NockLanguage.class, null, FunctionNode.DESCRIPTOR);
    this.root = root;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    FunctionNode.setSubject(frame, frame.getArguments()[0]);
    return root.executeGeneric(frame);
  }

}
