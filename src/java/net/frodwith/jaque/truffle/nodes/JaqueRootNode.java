package net.frodwith.jaque.truffle.nodes;

import net.frodwith.jaque.truffle.NockLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public class JaqueRootNode extends RootNode {
  @Child private FunctionNode root;
  @Child private DispatchNode jump;
  
  public JaqueRootNode(FunctionNode root) {
    super(NockLanguage.class, null, FunctionNode.DESCRIPTOR);
    this.root = root;
    this.jump = DispatchNodeGen.create();
  }

  @Override
  public Object execute(VirtualFrame frame) {
    Object subject = frame.getArguments()[0];
    FunctionNode.setSubject(frame, subject);
    return root.executeGeneric(frame);
  }

}
