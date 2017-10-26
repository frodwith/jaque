package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public abstract class AesCbcNode extends SampleContextNode {
  public abstract Object executeCbc(VirtualFrame frame, Object key, Object iv, Object msg);
  
  public Object doSampleContext(VirtualFrame frame, Object sample, Object context) {
    Cell conSam = Cell.orBail(Cell.orBail(Cell.orBail(context).tail).head);
    return executeCbc(frame, conSam.head, conSam.tail, sample);
  }

}