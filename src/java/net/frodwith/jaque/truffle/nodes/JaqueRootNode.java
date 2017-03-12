package net.frodwith.jaque.truffle.nodes;

import net.frodwith.jaque.truffle.NockLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import net.frodwith.jaque.truffle.nodes.formula.Formula;

public class JaqueRootNode extends RootNode {
  @Child private Formula root;
  
  public JaqueRootNode(Formula root) {
    super(NockLanguage.class, null, JaqueNode.DESCRIPTOR);
    this.root = root;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    Object subject = frame.getArguments()[0];
    JaqueNode.setSubject(frame, subject);
    return root.executeSafe(frame);
  }

}
