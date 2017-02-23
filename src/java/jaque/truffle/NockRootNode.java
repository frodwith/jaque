package jaque.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;

@NodeInfo(shortName="root")
public class NockRootNode extends RootNode {
  @Child private Formula f;
  
  public NockRootNode(Formula f) {
    super(NockLanguage.class, null, null);
    this.f = f;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return f.execute(frame);
  }

}
