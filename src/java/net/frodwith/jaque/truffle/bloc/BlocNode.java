package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.truffle.nodes.JaqueNode;

public abstract class BlocNode extends JaqueNode {
  public static final FrameDescriptor DESCRIPTOR;
  protected static final FrameSlot STACK;

  static {
    DESCRIPTOR = new FrameDescriptor();
    STACK = DESCRIPTOR.addFrameSlot("stack");
    STACK.setKind(FrameSlotKind.Object);
  }

  @SuppressWarnings("unchecked")
  public static Stack<Object> getStack(VirtualFrame frame) {
    try {
      return (Stack<Object>) frame.getObject(STACK);
    }
    catch (FrameSlotTypeException e) {
      throw new Bail();
    }
  }
}
