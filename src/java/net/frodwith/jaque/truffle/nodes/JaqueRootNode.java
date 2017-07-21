package net.frodwith.jaque.truffle.nodes;

import java.util.Stack;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

import net.frodwith.jaque.truffle.NockLanguage;

public class JaqueRootNode extends RootNode {
  @Child public FunctionNode root;
  private final String name;
  private final Object axis; // unused
  
  public JaqueRootNode(FunctionNode root) {
    this(root, null, null);
  }

  public JaqueRootNode(FunctionNode root, String name, Object axis) {
    super(NockLanguage.class, null, FunctionNode.DESCRIPTOR);
    this.root = root;
    this.name = name;
    this.axis = axis;
  }
  
  @Override
  public String getName() {
    if ( null != name ) {
      return name;
    }
    else {
      return super.getName();
    }
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    FunctionNode.setSubject(frame, frame.getArguments()[0]);
    return root.executeGeneric(frame);
  }

}
