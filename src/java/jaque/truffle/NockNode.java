package jaque.truffle;

import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;

import jaque.interpreter.Bail;
import jaque.noun.*;

@TypeSystemReference(NockTypes.class)
public abstract class NockNode extends Node {
  protected static NockContext getContext(VirtualFrame frame) {
    return (NockContext) frame.getArguments()[0];
  }
  
  protected static Object getSubject(VirtualFrame frame) {
    return NockLanguage.getSubject(frame);
  }

  protected static void setSubject(VirtualFrame frame, Object subject) {
    NockLanguage.setSubject(frame, subject);
  }
}
