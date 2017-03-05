package jaque.truffle;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;

import jaque.interpreter.Hint;
import jaque.noun.Cell;

public abstract class HintFormula extends SafeFormula {
  private final BranchProfile hintGivesProduct = BranchProfile.create();
  private final BranchProfile hintGivesNull = BranchProfile.create();
  protected abstract Hint executeHint(VirtualFrame frame);
  protected abstract Object executeNext(VirtualFrame frame);
  private final Cell src;
  
  protected HintFormula(Formula next) {
    this.src = next.toCell();
  }
  
  protected Cell source() {
    return src;
  }
  
  @Override
  public Object execute(VirtualFrame frame) {
    NockContext c = getContext(frame);
    Hint h = executeHint(frame);
    Object product = c.startHint(h);
    if ( null == product ) {
      hintGivesNull.enter();
      product = executeNext(frame);
      c.endHint(h, product);
    }
    else {
      hintGivesProduct.enter();
    }

    return product;
  }
}
