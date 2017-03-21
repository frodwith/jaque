package net.frodwith.jaque.truffle.nodes;

import net.frodwith.jaque.location.Location;
import net.frodwith.jaque.truffle.NockLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public class FastRootNode extends RootNode {
  @Child public FunctionNode root;
  private final Location location;
  
  public FastRootNode(FunctionNode root, Location location) {
    /* TODO: SourceSection with name can go here! Yay location! */
    super(NockLanguage.class, null, FunctionNode.DESCRIPTOR);
    this.root = root;
    this.location = location;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    FunctionNode.setSubject(frame, location.reconstruct(frame.getArguments()));
    return root.executeGeneric(frame);
  }

}
