package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import net.frodwith.jaque.truffle.Types;

@TypeSystemReference(Types.class)
public class JaqueNode extends Node {
  public static final FrameDescriptor DESCRIPTOR;
  private static final FrameSlot SUBJECT_SLOT;

  static {
    DESCRIPTOR = new FrameDescriptor();
    SUBJECT_SLOT = DESCRIPTOR.addFrameSlot("subject");
    SUBJECT_SLOT.setKind(FrameSlotKind.Object);
  }
  
  /* Nock's only local variable is the subject. */
      
  public static Object getSubject(VirtualFrame frame) {
    try {
      return frame.getObject(SUBJECT_SLOT);
    }
    catch (FrameSlotTypeException e) {
      throw new RuntimeException();
    }
  }

  public static void setSubject(VirtualFrame frame, Object subject) {
    frame.setObject(SUBJECT_SLOT, subject);
  }
}
