package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Fragmenter;

public class FragmentNode extends FormulaNode {
  private Fragmenter fragmenter;
  
  public FragmentNode(Fragmenter fragmenter) {
    this.fragmenter = fragmenter;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return fragmenter.fragment(getSubject(frame));
  }

}
