package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Noun;

public class FragmentNode extends SafeFormula {
  private Object axis;
  
  public FragmentNode(Object axis) {
    this.axis = axis;
  }

  @Override
  public Object executeSubject(VirtualFrame frame, Object subject) {
    return Noun.fragment(axis, subject);
  }

}
