package jaque.truffle;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;

@NodeInfo(shortName="root")
public class NockRootNode extends RootNode {
  @Child private Formula f;
  
  public NockRootNode(Formula f) {
    super(NockLanguage.class, null, NockLanguage.frameDescriptor);
    this.f = f;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    NockLanguage.setSubject(frame, frame.getArguments()[1]);
    return f.executeSafe(frame);
  }
}
