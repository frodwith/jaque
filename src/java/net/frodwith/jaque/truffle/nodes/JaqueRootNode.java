package net.frodwith.jaque.truffle.nodes;

import net.frodwith.jaque.truffle.NockLanguage;
import net.frodwith.jaque.truffle.TailException;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.profiles.BranchProfile;

public class JaqueRootNode extends RootNode {
  @Child public FunctionNode root;
  
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
