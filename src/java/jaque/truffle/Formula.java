package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Formula extends NockNode {
  
  public abstract Cell toCell();
  public abstract Object execute(VirtualFrame frame);
  public abstract Object executeSafe(VirtualFrame frame);
  private static final Atom maxLongAtom = Atom.fromLong(Long.MAX_VALUE);
  
  public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectLong(executeSafe(frame));
  }

  public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectBoolean(executeSafe(frame));
  }

  public Atom executeAtom(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectAtom(executeSafe(frame));
  }

  public Cell executeCell(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectCell(executeSafe(frame));
  }
}
