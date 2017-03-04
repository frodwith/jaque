package jaque.truffle;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;

@NodeInfo(shortName="root")
public class NockRootNode extends RootNode {
  @Child private Formula f;
  private final IndirectCallNode callNode;
  
  public NockRootNode(Formula f) {
    super(NockLanguage.class, null, null);
    this.f = f;
    this.callNode = Truffle.getRuntime().createIndirectCallNode();
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return f.executeSafe(frame);
  }
}
