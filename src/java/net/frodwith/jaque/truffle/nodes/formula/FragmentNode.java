package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Noun;

public class FragmentNode extends SafeFormula {
  private Object axis;
  
  public FragmentNode(Object axis) {
    this.axis = axis;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return Noun.fragment(axis, getSubject(frame));
  }

}
