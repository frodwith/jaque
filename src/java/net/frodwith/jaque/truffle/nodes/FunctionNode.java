package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.object.DynamicObject;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.TypesGen;

/* Execute a function of the subject (stored in frame)
 * return some noun as a product
 */
public abstract class FunctionNode extends JaqueNode {
  public static final FrameDescriptor DESCRIPTOR;
  protected static final FrameSlot SUBJECT_SLOT;

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

  public abstract Object executeGeneric(VirtualFrame frame);

  public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
    return TypesGen.expectLong(executeGeneric(frame));
  }

  public int[] executeIntArray(VirtualFrame frame) throws UnexpectedResultException {
    return TypesGen.expectIntArray(executeGeneric(frame));
  }
  
  public DynamicObject executeCell(VirtualFrame frame) throws UnexpectedResultException {
    Object o = executeGeneric(frame);
    if ( Noun.isCell(o) ) {
      return Noun.asCell(o);
    }
    else {
      throw new UnexpectedResultException(o);
    }
  }

}
