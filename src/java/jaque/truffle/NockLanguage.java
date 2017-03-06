package jaque.truffle;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import jaque.interpreter.Bail;
import jaque.interpreter.Machine;
import jaque.interpreter.Result;
import jaque.noun.Cell;
import jaque.noun.Noun;

public class NockLanguage extends TruffleLanguage<NockContext> {
  public static final FrameDescriptor frameDescriptor;
  private static final FrameSlot subjectSlot;
  
  static {
    frameDescriptor = new FrameDescriptor();
    subjectSlot = frameDescriptor.addFrameSlot("subject");
    subjectSlot.setKind(FrameSlotKind.Object);
  }

  public static final Result nock(Machine m, Noun subject, Cell formula) {
    NockContext c = new NockContext(m);
    DirectCallNode callNode = DirectCallNode.create(c.getNockTarget(formula));
    Object[] arguments = new Object[] {c, subject};
    VirtualFrame frame = Truffle.getRuntime().createVirtualFrame(arguments, frameDescriptor);
    Object result = callNode.call(frame, arguments);
    return new Result(c.m, Noun.coerceNoun(result));
  }
  
  public static final void setSubject(VirtualFrame frame, Object subject) {
    frame.setObject(subjectSlot, subject);
  }
  
  public static final Object getSubject(VirtualFrame frame) {
    try {
      return frame.getObject(subjectSlot);
    } 
    catch (FrameSlotTypeException e) {
      throw new RuntimeException();
    }
  }

  @Override
  protected NockContext createContext(com.oracle.truffle.api.TruffleLanguage.Env env) {
    // TODO: This isn't quite implemented...
    return null;
  }

  @Override
  protected Object findExportedSymbol(NockContext context, String globalName, boolean onlyExplicit) {
    // no symbols are exported
    return null;
  }

  @Override
  protected Object getLanguageGlobal(NockContext context) {
    // there are no language globals
    return null;
  }

  @Override
  protected boolean isObjectOfLanguage(Object object) {
    return false;
  }

}
