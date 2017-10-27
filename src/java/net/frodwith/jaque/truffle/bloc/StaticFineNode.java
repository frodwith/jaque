package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Noun;

public final class StaticFineNode extends FineOpNode {
  private final Object constant;
  
  public StaticFineNode(Object constant) {
    this.constant = constant;
  }
  
  @Override
  public boolean executeFine(VirtualFrame frame, Object got) {
    return Noun.equals(constant, got);
  }
}
