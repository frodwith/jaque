package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;

public abstract class AesEcbNode extends SampleContextNode {
  public abstract Object executeEcb(VirtualFrame frame, Object key, Object block);
  
  public Object doSampleContext(VirtualFrame frame, Object sample, Object context) {
    Object conSam = Cell.expect(Cell.expect(context).tail).head;
    return executeEcb(frame, conSam, sample);
  }

}