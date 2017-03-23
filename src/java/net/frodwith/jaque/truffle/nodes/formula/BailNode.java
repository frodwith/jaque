package net.frodwith.jaque.truffle.nodes.formula;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.Bail;

public class BailNode extends FormulaNode {
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    throw new Bail();
  }
}
