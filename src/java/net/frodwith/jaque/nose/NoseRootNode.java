package net.frodwith.jaque.nose;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import net.frodwith.jaque.truffle.NockLanguage;

public class NoseRootNode extends RootNode {
  
  public NoseRootNode() {
    super(NockLanguage.class, null, null);
  }

  @Override
  public Object execute(VirtualFrame frame) {
    // TODO Auto-generated method stub
    return null;
  }

}
