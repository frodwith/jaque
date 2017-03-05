package jaque.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;

  // Formulae who do not throw jump exceptions
public abstract class SafeFormula extends Formula {
  @Override
  public Object executeSafe(VirtualFrame frame) {
    return this.execute(frame);
  }
}
