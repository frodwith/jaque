package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.truffle.FragmentationException;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;

public final class FragmentNode extends FormulaNode {
  @Child private FragmentationNode f;
  
  public FragmentNode(Object axis) {
    this.f = new FragmentationNode(axis);
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    try {
      return f.executeFragment(getSubject(frame));
    }
    catch ( FragmentationException e ) {
      throw new Bail();
    }
  }

}
