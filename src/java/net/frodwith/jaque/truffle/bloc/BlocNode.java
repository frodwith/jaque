package net.frodwith.jaque.truffle.bloc;

import java.util.Deque;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import net.frodwith.jaque.truffle.Types;

@TypeSystemReference(Types.class)
public abstract class BlocNode extends Node {
  public static final FrameDescriptor DESCRIPTOR;
  protected static final FrameSlot STACK;

  static {
    DESCRIPTOR = new FrameDescriptor();
    STACK = DESCRIPTOR.addFrameSlot("stack");
    STACK.setKind(FrameSlotKind.Object);
  }

  @SuppressWarnings("unchecked")
  public static Deque<Object> getStack(VirtualFrame frame) {
    try {
      return (Deque<Object>) frame.getObject(STACK);
    }
    catch (FrameSlotTypeException e) {
      throw new RuntimeException("fatal frame slot error");
    }
  }
}
